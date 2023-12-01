/*

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Product {
    private String name;
    private String description;
    private int quantity;
    private double price;

    public Product(String name, String description, int quantity, double price) {
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

    public void SetQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public JSONObject toJSON() {
        JSONObject jsonProduct = new JSONObject();
        jsonProduct.put("name", name);
        jsonProduct.put("description", description);
        jsonProduct.put("quantity", quantity);
        jsonProduct.put("price", price);
        return jsonProduct;
    }
}

class Store {
    private String name;
    private List<Product> products;
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

    public List<Product> getProducts() {
        return products;
    }

    public int addProduct(Product product) {
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

    public JSONObject toJSON() {
        lock.readLock().lock();
        try {
            JSONObject jsonStore = new JSONObject();
            jsonStore.put("name", name);

            JSONArray jsonProducts = new JSONArray();
            for (Product product : products) {
                jsonProducts.add(product.toJSON());
            }
            jsonStore.put("products", jsonProducts);

            return jsonStore;
        } finally {
            lock.readLock().unlock();
        }
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
        map.put(-3,"Store not found. Please check the name and try again.");
        map.put(-4, "No stores found for the owner. Please create a store first.");
        map.put(-5, "The store doesn't have enough the products");
        map.put(-6, "The product price in the store is greater than the original price");

        // Make the map unmodifiable to ensure it remains constant
        ERROR_CODE_MAP = Collections.unmodifiableMap(map);
    }

    static String get(int errCode)
    {
        return ERROR_CODE_MAP.get(errCode);
    }
}
/*
public class MarketData implements IMarketData {

    private final String DATA_FILE = "market_data.json";
    private final JSONParser jsonParser = new JSONParser();
    private Map<String, List<Store>> ownerStoresMap = new HashMap<>();
    private List<String> storeNames = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private ILog log;

    MarketData(ILog log)
    {
        this.log = log;
    }

    @Override
    public int AddStore(String seller, String storeName)
    {
        lock.writeLock().lock();
        try {
            if(storeNames.contains(storeName))
            {
                return -2;
            }
            storeNames.add(storeName);
            List<Store> stores = ownerStoresMap.computeIfAbsent(seller, k -> new ArrayList<>());
            Store store = new Store(storeName);
            stores.add(store);
            return 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int AddProduct(String seller, String storeName, String productName, String description, int quantity, double price) {
        lock.writeLock().lock();
        try {
            List<Store> stores = ownerStoresMap.get(seller);
            if (stores == null) {
                return -4;
            }

            Store store = findStoreByName(stores, storeName);
            if (store != null) {
                Product product = new Product(productName, description, quantity, price);
                return store.addProduct(product);
            } else {
                return -3;
            }
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<productBrowseResult> BrowseProduct(String seachKeyWords){

        List<productBrowseResult> resultSet = new ArrayList<>();
        lock.readLock().lock();
        try {
            for (Map.Entry<String, List<Store>> entry : ownerStoresMap.entrySet()) {
                String seller = entry.getKey();
                List<Store> stores = ownerStoresMap.get(entry.getKey());
                for (Store store : stores) {
                    for (Product product : store.getProducts()) {
                        if(product.getName().contains(seachKeyWords) || product.getDescription().contains(seachKeyWords)) {
                            productBrowseResult productResult = new productBrowseResult(seller, store.getName(), product.getName(),
                                    product.getDescription(), product.getQuantity(), product.getPrice());
                            resultSet.add(productResult);
                        }
                    }
                }
            }

            return resultSet;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int BuyProduct(String seller, String storeName, String productName, int quantity, double price)
    {
        lock.writeLock().lock();
        try {
            List<Store> stores = ownerStoresMap.get(seller);
            for (Store store : stores) {
                if(store.getName().equals(storeName))
                {
                    for (Product product : store.getProducts()) {
                        if(product.getName().equals(productName)) {
                            if(product.getQuantity() < quantity)
                            {
                                return -5;
                            }

                            if(product.getPrice() > price)
                            {
                                return -6;
                            }

                            product.SetQuantity(product.getQuantity() - quantity);

                            saveData();
                        }
                    }
                }
            }
            return 0;
        }
        finally {
            lock.writeLock().unlock();
        }

    }

    private void saveData() {
    }

    private void loadData() {
        lock.writeLock().lock();
        try {
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Store findStoreByName(List<Store> stores, String storeName) {
        for (Store store : stores) {
            if (store.getName().equals(storeName)) {
                return store;
            }
        }
        return null;
    }*/

