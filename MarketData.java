import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// for server side
class ProductMarketPlace {
    private String name;
    private String description;
    private int quantity;
    private double price;

    public ProductMarketPlace(String name, String description, int quantity, double price) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void SetQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }

    public static JSONArray productsToJsonArray(List<ProductMarketPlace> products) {
        JSONArray productList = new JSONArray();

        for (ProductMarketPlace product : products) {
            JSONObject productJson = new JSONObject();
            productJson.put("name", product.getName());
            productJson.put("description", product.getDescription());
            productJson.put("quantity", product.getQuantity());
            productJson.put("price", product.getPrice());
            productList.add(productJson);
        }

        return productList;
    }

    public static List<ProductMarketPlace> productsFromJsonArray(JSONArray jsonArray) {
        List<ProductMarketPlace> products = new ArrayList<>();

        for (Object productObj : jsonArray) {
            JSONObject productJson = (JSONObject) productObj;
            String name = (String) productJson.get("name");
            String description = (String) productJson.get("description");
            int quantity = ((Long) productJson.get("quantity")).intValue();
            double price = (double) productJson.get("price");
            products.add(new ProductMarketPlace(name, description, quantity, price));
        }
        return products;
    }
}

class Store {
    private String name;
    private List<ProductMarketPlace> products;
    private Set<String> productNames; // To ensure unique product names
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public Store(String name) {
        this.name = name;
        this.products = new ArrayList<>();
        this.productNames = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public List<ProductMarketPlace> getProducts() {
        return products;
    }

    public int addProduct(ProductMarketPlace product) {
        lock.writeLock().lock();
        try {
            if (productNames.contains(product.getName())) {
                return -1;
            } else {
                products.add(product);
                productNames.add(product.getName());
                return 0;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeProductName(ProductMarketPlace product) {
        lock.writeLock().lock();
        try {
            if (productNames.contains(product.getName())) {
                productNames.remove(product.getName());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        return "Store{" +
                "name='" + name + '\'' +
                ", products=" + products +
                '}';
    }

    public static JSONArray storesToJsonArray(List<Store> stores) {
        JSONArray storeList = new JSONArray();

        for (Store store : stores) {
            JSONObject storeJson = new JSONObject();
            storeJson.put("name", store.getName());
            storeJson.put("products", ProductMarketPlace.productsToJsonArray(store.getProducts()));
            storeList.add(storeJson);
        }

        return storeList;
    }

}

class ShoppingCartRecord {
    public String sellerName;
    public String storeName;
    public String productName;
    public int quantity;
    public double price;

    public ShoppingCartRecord(String sellerName, String storeName, String productName, int quantity, double price) {
        this.sellerName = sellerName;
        this.storeName = storeName;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public static JSONArray ShoppingCartRecordsToJsonArray(List<ShoppingCartRecord> shoppingCartRecords) {
        JSONArray jShoppingCartRecords = new JSONArray();

        for (ShoppingCartRecord shoppingCartRecord : shoppingCartRecords) {
            JSONObject shoppingCartRecordJson = new JSONObject();
            shoppingCartRecordJson.put("seller", shoppingCartRecord.sellerName);
            shoppingCartRecordJson.put("store", shoppingCartRecord.storeName);
            shoppingCartRecordJson.put("product", shoppingCartRecord.productName);
            shoppingCartRecordJson.put("quantity", shoppingCartRecord.quantity);
            shoppingCartRecordJson.put("price", shoppingCartRecord.price);

            jShoppingCartRecords.add(shoppingCartRecordJson);
        }

        return jShoppingCartRecords;
    }
}

class ErrorcodeMap {
    private static final Map<Integer, String> ERROR_CODE_MAP;

    static {
        // Create and initialize the map in a static block
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "success");
        map.put(-1, "Product with the same name already exists in the store. Please choose a different name.");
        map.put(-2, "Store name already exists. Please choose a different name.");
        map.put(-3, "Store not found. Please check the name and try again.");
        map.put(-4, "No stores found for the owner. Please create a store first.");
        map.put(-5, "The store doesn't have enough the products");
        map.put(-6, "The product proce in the store is greater than the original price");
        map.put(-7, "The product is not found in the store");

        // Make the map unmodifiable to ensure it remains constant
        ERROR_CODE_MAP = Collections.unmodifiableMap(map);
    }

    static String get(int errCode) {
        return ERROR_CODE_MAP.get(errCode);
    }
}

public class MarketData implements IMarketData {

    private final String DATA_FILE = "market_data.json";
    private Map<String, List<Store>> ownerStoresMap = new HashMap<>();

    private final String SALE_DATA_FILE = "sales_data.json";
    private List<SaleRecord> salesRecordList = new ArrayList<>();

    private final String SHOPPINGCART_DATA_FILE = "shoppingcart_data.json";
    private Map<String, List<ShoppingCartRecord>> buyerCartMap = new HashMap<>();

    private List<String> storeNames = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final JSONParser jsonParser = new JSONParser();

    private ILog log;

    MarketData(ILog log) {
        this.log = log;
        init();
    }

    public void init() {
        loadData();
    }

    @Override
    public int AddStore(String seller, String storeName) {
        lock.writeLock().lock();
        try {
            if (storeNames.contains(storeName)) {
                return -2;
            }
            storeNames.add(storeName);
            List<Store> stores = ownerStoresMap.computeIfAbsent(seller, k -> new ArrayList<>());
            Store store = new Store(storeName);
            stores.add(store);

            saveData();

            return 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<String> ListStore(String seller) {
        List<String> resultSet = new ArrayList<>();
        lock.readLock().lock();
        try {

            log.log(seller, "List Stores", seller);
            List<Store> stores = ownerStoresMap.get(seller);
            if (stores == null) {
                return resultSet;
            }

            for (Store store : stores) {
                resultSet.add(store.getName());
            }
            return resultSet;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int RemoveProduct(String seller, String storeName, String productName) {
        lock.writeLock().lock();
        try {
            log.log(seller, "RemoveProd:GetStores", seller);
            List<Store> stores = ownerStoresMap.get(seller);
            if (stores == null) {
                log.log(seller, "RemoveProd:GetStores failes", seller);
                return -4;
            }

            log.log(seller, "RemoveProd:FindStore", storeName);
            Store store = findStoreByName(stores, storeName);
            if (store != null) {
                for (ProductMarketPlace product : store.getProducts()) {
                    if (product.getName().equals(productName)) {
                        store.getProducts().remove(product);
                        store.removeProductName(product);
                        saveData();
                        return 0;
                    }
                }
                return -7;
            } else {
                return -3;
            }
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public int AddProduct(String seller, String storeName, String productName, String description, int quantity,
                          double price) {
        lock.writeLock().lock();
        try {
            log.log(seller, "AddProd:GetStores", seller);
            List<Store> stores = ownerStoresMap.get(seller);
            if (stores == null) {
                log.log(seller, "AddProd:GetStores failes", seller);
                return -4;
            }

            log.log(seller, "AddProd:FindStore", storeName);
            Store store = findStoreByName(stores, storeName);
            if (store != null) {
                ProductMarketPlace product = new ProductMarketPlace(productName, description, quantity, price);
                int ret = store.addProduct(product);
                if (ret == 0) {
                    saveData();
                }
                return ret;
            } else {
                return -3;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<productBrowseResult> ListStoreProducts(String seller, String storeName) {
        List<productBrowseResult> resultSet = new ArrayList<>();
        lock.readLock().lock();
        try {
            log.log(seller, "List Store Pruducts", storeName);
            List<Store> stores = ownerStoresMap.get(seller);
            if (stores == null) {
                return resultSet;
            }

            for (Store store : stores) {
                if (store.getName().equals(storeName)) {
                    for (ProductMarketPlace product : store.getProducts()) {
                        productBrowseResult productResult = new productBrowseResult(seller, store.getName(),
                                product.getName(),
                                product.getDescription(), product.getQuantity(), product.getPrice());
                        resultSet.add(productResult);
                    }
                }
            }

            return resultSet;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<productBrowseResult> BrowseProduct(String seachKeyWords) {

        List<productBrowseResult> resultSet = new ArrayList<>();
        lock.readLock().lock();
        try {
            log.log("buyer", "Search Pruducts", seachKeyWords);
            String[] words = seachKeyWords.split("\\s+");
            for (Map.Entry<String, List<Store>> entry : ownerStoresMap.entrySet()) {
                String seller = entry.getKey();
                List<Store> stores = ownerStoresMap.get(entry.getKey());
                for (Store store : stores) {
                    for (ProductMarketPlace product : store.getProducts()) {
                        if (containsAnyWord(product.getName(), words)
                                || containsAnyWord(product.getDescription(), words)) {
                            productBrowseResult productResult = new productBrowseResult(seller, store.getName(),
                                    product.getName(),
                                    product.getDescription(), product.getQuantity(), product.getPrice());
                            resultSet.add(productResult);
                        }
                    }
                }
            }

            return resultSet;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<SaleRecord> querybuyerPurchaseRecords(String buyer) {
        // salesRecordList
        List<SaleRecord> resultSet = new ArrayList<>();
        lock.readLock().lock();
        try {
            log.log(buyer, "purchase history", "");
            for (SaleRecord saleRecord : salesRecordList) {

                if (saleRecord.buyer.equals(buyer)) {
                    resultSet.add(saleRecord);
                }
            }

            return resultSet;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<ShoppingCartRecord> readShoppingCart(String buyer) {
        lock.readLock().lock();
        try {
            log.log(buyer, "read shoppingCart", "");
            List<ShoppingCartRecord> shoppingcart = buyerCartMap.computeIfAbsent(buyer, k -> new ArrayList<>());
            return shoppingcart;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int BuyProduct(String seller, String buyer, String storeName, String productName, int quantity,
                          double price) {
        lock.writeLock().lock();
        try {
            List<Store> stores = ownerStoresMap.get(seller);
            for (Store store : stores) {
                if (store.getName().equals(storeName)) {
                    for (ProductMarketPlace product : store.getProducts()) {
                        if (product.getName().equals(productName)) {
                            if (product.getQuantity() < quantity) {
                                return -5;
                            }

                            if (product.getPrice() != price) {
                                return -6;
                            }

                            product.SetQuantity(product.getQuantity() - quantity);
                            SaleRecord saleRecord = new SaleRecord(buyer, LocalDateTime.now(), seller, storeName,
                                    productName, quantity, price);
                            salesRecordList.add(saleRecord);
                            saveData();
                        }
                    }
                }
            }
            return 0;
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public int AddShoppingCart(String buyer, String seller, String storeName, String productName, int quantity,
                               double price) {
        lock.writeLock().lock();
        try {
            List<ShoppingCartRecord> shoppingcart = buyerCartMap.computeIfAbsent(buyer, k -> new ArrayList<>());

            ShoppingCartRecord record = new ShoppingCartRecord(seller, storeName, productName, quantity, price);
            shoppingcart.add(record);
            saveShoppingCartData();

            return 0;
        } finally {
            lock.writeLock().unlock();
        }

    }


    public int ShoppingCartCommit(String buyer)
    {
        lock.writeLock().lock();
        int ret = 0;
        try {
            List<ShoppingCartRecord> shoppingcart = buyerCartMap.computeIfAbsent(buyer, k -> new ArrayList<>());
            for(ShoppingCartRecord item: shoppingcart)
            {
                int r = BuyProduct(item.sellerName, buyer, item.storeName, item.productName, item.quantity, item.price);
                if(r != 0)
                {
                    ret = r;
                }
            }
            shoppingcart.clear();

            saveData();
            return ret;
        } finally {
            lock.writeLock().unlock();
        }

    }

    private void saveMarketData() {
        JSONArray ownerList = new JSONArray();
        for (Map.Entry<String, List<Store>> entry : ownerStoresMap.entrySet()) {
            JSONObject ownerStoreJson = new JSONObject();
            List<Store> stores = ownerStoresMap.get(entry.getKey());
            ownerStoreJson.put("seller", entry.getKey());
            ownerStoreJson.put("stores", Store.storesToJsonArray(stores));
            ownerList.add(ownerStoreJson);

        }

        try (FileWriter fileWriter = new FileWriter(DATA_FILE)) {
            fileWriter.write(ownerList.toJSONString());
            log.log("sys", "[save file]", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveShoppingCartData() {
        JSONArray ownerList = new JSONArray();
        for (Map.Entry<String, List<ShoppingCartRecord>> entry : buyerCartMap.entrySet()) {
            JSONObject ownerJson = new JSONObject();
            List<ShoppingCartRecord> shoppingCarts = buyerCartMap.get(entry.getKey());
            ownerJson.put("buyer", entry.getKey());
            ownerJson.put("shoppingcart", ShoppingCartRecord.ShoppingCartRecordsToJsonArray(shoppingCarts));
            ownerList.add(ownerJson);
        }

        try (FileWriter fileWriter = new FileWriter(SHOPPINGCART_DATA_FILE)) {
            fileWriter.write(ownerList.toJSONString());
            log.log("sys", "[save shoppingcart file]", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSalesData() {
        JSONArray salesList = SaleRecord.salesToJsonArray(salesRecordList);

        try (FileWriter fileWriter = new FileWriter(SALE_DATA_FILE)) {
            fileWriter.write(salesList.toJSONString());
            log.log("sys", "[save sales file]", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        lock.writeLock().lock();
        try {
            saveMarketData();
            saveSalesData();
            saveShoppingCartData();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void loadData() {
        lock.writeLock().lock();
        try {
            loadMarketData();
            loadSalesData();
            loadShoppingCartData();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void loadSalesData() {
        try {
            Reader reader = new FileReader(SALE_DATA_FILE);
            JSONArray jsonSales = (JSONArray) jsonParser.parse(reader);
            for (Object obj : jsonSales) {
                JSONObject jSaleRecordObj = (JSONObject) obj;
                String buyerName = (String) jSaleRecordObj.get("buyer");
                String dateTimeString = (String) jSaleRecordObj.get("datetime");
                try {
                    //LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    String sellerName = (String) jSaleRecordObj.get("seller");
                    String storeName = (String) jSaleRecordObj.get("store");
                    String product = (String) jSaleRecordObj.get("product");
                    int quantity = ((Long) jSaleRecordObj.get("quantity")).intValue();
                    double price = (double) jSaleRecordObj.get("price");

                    SaleRecord saleRecord = new SaleRecord(buyerName, null, sellerName, storeName, product, quantity,
                            price);
                    salesRecordList.add(saleRecord);
                } catch (DateTimeParseException e) {
                    System.err.println("Error parsing date-time string: " + dateTimeString);
                    e.printStackTrace();
                }
            }
        } catch (IOException | ParseException e) {
            // File does not exist or cannot be read, ignore and start with empty data
            log.log("sys", "loadSalesData", "failed");
            e.printStackTrace();
        }
    }


    private void loadMarketData() {
        try {
            Reader reader = new FileReader(DATA_FILE);
            JSONArray jsonSellers = (JSONArray) jsonParser.parse(reader);
            for (Object obj : jsonSellers) {
                JSONObject jsonOwner = (JSONObject) obj;
                String sellerName = (String) jsonOwner.get("seller");
                Set<String> storeNames = new HashSet<>();
                JSONArray jsonStores = (JSONArray) jsonOwner.get("stores");
                List<Store> stores = new ArrayList<>();

                for (Object storeObj : jsonStores) {
                    JSONObject jsonStore = (JSONObject) storeObj;
                    String storeName = (String) jsonStore.get("name");

                    if (!storeNames.contains(storeName)) {
                        storeNames.add(storeName);

                        Store store = new Store(storeName);

                        JSONArray jsonProducts = (JSONArray) jsonStore.get("products");
                        for (Object productObj : jsonProducts) {
                            JSONObject jsonProduct = (JSONObject) productObj;
                            ProductMarketPlace product = new ProductMarketPlace(
                                    (String) jsonProduct.get("name"),
                                    (String) jsonProduct.get("description"),
                                    ((Long) jsonProduct.get("quantity")).intValue(),
                                    (Double) jsonProduct.get("price"));
                            store.addProduct(product);
                        }

                        stores.add(store);
                    }
                }

                ownerStoresMap.put(sellerName, stores);
            }

        } catch (IOException | ParseException e) {
            // File does not exist or cannot be read, ignore and start with empty data
            log.log("sys", "loadMarketata", "failed");
        }
    }

    private void loadShoppingCartData() {
        try {
            Reader reader = new FileReader(SHOPPINGCART_DATA_FILE);
            JSONArray jsonbuyers = (JSONArray) jsonParser.parse(reader);
            for (Object obj : jsonbuyers) {
                JSONObject jsonOwner = (JSONObject) obj;
                String buyerName = (String) jsonOwner.get("buyer");
                JSONArray jsoncartrecords = (JSONArray) jsonOwner.get("shoppingcart");
                List<ShoppingCartRecord> shoppingCartrecords = new ArrayList<>(); // Initialize the list

                for (Object cartrecordobj : jsoncartrecords) {
                    JSONObject jCartrecordobj = (JSONObject) cartrecordobj;

                    ShoppingCartRecord shoppingcartRecords = new ShoppingCartRecord(
                            (String) jCartrecordobj.get("seller"),
                            (String) jCartrecordobj.get("store"),
                            (String) jCartrecordobj.get("product"),
                            ((Long) jCartrecordobj.get("quantity")).intValue(),
                            (Double) jCartrecordobj.get("price"));

                    shoppingCartrecords.add(shoppingcartRecords);
                }

                buyerCartMap.put(buyerName, shoppingCartrecords);
            }

        } catch (IOException | ParseException e) {
            // File does not exist or cannot be read, ignore and start with empty data
            log.log("sys", "loadShoppingCartData", "failed");
        }
    }


    private Store findStoreByName(List<Store> stores, String storeName) {
        for (Store store : stores) {
            if (store.getName().equals(storeName)) {
                return store;
            }
        }
        return null;
    }

    private static boolean containsAnyWord(String input, String[] wordArray) {
        for (String word : wordArray) {
            if (input.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
