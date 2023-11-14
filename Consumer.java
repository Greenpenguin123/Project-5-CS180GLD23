import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Consumer {
    private String userName;
    private String cartFile;
    private String purchaseHistroyFile;
    private ArrayList<Product> productList = getProductList();

    public Consumer(String userName) {
        this.userName = userName;
        this.cartFile = userName + "Cart.csv";
        this.purchaseHistroyFile = userName + "PurchaseHistory.csv";
    }

    public static ArrayList<Product> viewMarketPlace() {
        ArrayList<Product> products;
        products = getProductList();

        for (Product product : products) {
            System.out.println(product);
        }
        return products;
    }
    private static ArrayList<Product> getProductList() {
        ArrayList<Product> products = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("MarketPlace.csv"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 5) {
                    String name = values[0];
                    String description = values[1];
                    String store = values[2];
                    double price = Double.parseDouble(values[3]);
                    int quantityAvailable = Integer.parseInt(values[4]);
                    products.add(new Product(name, description, store, price, quantityAvailable));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static void searchProducts(ArrayList<Product> products, Scanner input) {
        ArrayList<Product> matchedProducts = new ArrayList<>();
        System.out.println("What would you like to search?");
        String keyword = input.nextLine();

        for (Product product : products) {
            if (product.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                    product.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                    product.getStore().toLowerCase().contains(keyword.toLowerCase())) {
                matchedProducts.add(product);
            }
        }

        if (matchedProducts.isEmpty()) {
            System.out.println("No products found matching your search.");
        } else {
            for (Product product : matchedProducts) {
                System.out.println(product);
            }
        }
    }

    public void addProduct(Product product) {
        try (FileWriter fw = new FileWriter(cartFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            String line = product.getName() + "," +
                    product.getDescription() + "," +
                    product.getStore() + "," +
                    product.getPrice() + "," +
                    product.getQuantityAvailable() + "\n";

            bw.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeProduct(String productName) {
        File inputFile = new File(cartFile);
        File tempFile = new File("tempCart.csv");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String trimmedLine = currentLine.trim();
                if (trimmedLine.startsWith(productName + ",")) {
                    continue;
                }
                writer.write(currentLine + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inputFile.delete()) {
            System.out.println("Could not delete the original file");
            return;
        }

        if (!tempFile.renameTo(inputFile)) {
            System.out.println("Could not rename the temp file to original file name");
        }
    }

    public ArrayList<Product> purchase() {
        ArrayList<Product> purchasedProducts = new ArrayList<>();
        String timeStamp = new SimpleDateFormat("MM/dd/yyyy_HH:mm").format(Calendar.getInstance().getTime());
        boolean purchaseSuccessful = true;

        try (BufferedReader br = new BufferedReader(new FileReader(cartFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(purchaseHistroyFile, true))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] productDetails = line.split(",");
                if (productDetails.length >= 5) {
                    String name = productDetails[0];
                    String description = productDetails[1];
                    String store = productDetails[2];
                    double price = Double.parseDouble(productDetails[3]);

                    Product purchasedProduct = findProduct(name, description, store);
                    if (purchasedProduct != null) {
                        if (purchasedProduct.getQuantityAvailable() >= 1) {
                            purchasedProducts.add(purchasedProduct);
                            System.out.println("Quantitiy1: " + purchasedProduct.getQuantityAvailable());
                            purchasedProduct.setQuantityAvailable(purchasedProduct.getQuantityAvailable() - 1);
                            System.out.println("Quantitiy2: " + purchasedProduct.getQuantityAvailable());
                            purchasedProduct.incrementItemsSoldBy(1);

                            bw.write(timeStamp + "," + name + "," + description + "," + store + "," + price + "," + 1 + "\n");
                        } else {
                            System.out.println("Not enough stock for " + name + ". Available: " + purchasedProduct.getQuantityAvailable() + ", Requested: " + 1);
                            purchaseSuccessful = false;
                        }
                    }
                }
            }

            clearCart();

        } catch (IOException e) {
            e.printStackTrace();
            purchaseSuccessful = false;
        }

        if (purchaseSuccessful) {
            System.out.println("Thank You For Purchasing!");
            updateMarketplaceFile();
        } else {
            System.out.println("Purchase partially successful or failed due to insufficient stock.");
        }

        return purchasedProducts;
    }
    private void updateMarketplaceFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("MarketPlace.csv"))) {
            writer.write("Name,Description,Store,Price,Quantity Available\n");
            for (Product product : productList) {
                writer.write(String.format("%s,%s,%s,%.2f,%d\n",
                        product.getName(),
                        product.getDescription(),
                        product.getStore(),
                        product.getPrice(),
                        product.getQuantityAvailable()));
                System.out.println("Quanitiy:" + product.getQuantityAvailable());

            }
        } catch (IOException e) {
            System.err.println("Error while updating the marketplace file: " + e.getMessage());
        }
    }


    private void clearCart() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(cartFile))) {
            pw.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Product findProduct(String name, String description, String store) {
        ArrayList<Product> products;
        products = getProductList();
        for (Product product : products) {
            if (product.getName().equals(name) && product.getDescription().equals(description) && product.getStore().equals(store)) {
                return product;
            }
        }
        return null;
    }

    private void updateProductQuantity(Product purchasedProduct, int quantity) {
        purchasedProduct.setQuantityAvailable(purchasedProduct.getQuantityAvailable() - quantity);
    }
    public void sortMarketPlace(ArrayList<Product> products, Scanner input) {
        String choice = "";
        while (!choice.equalsIgnoreCase("P") && !choice.equalsIgnoreCase("Q")) {
            System.out.println("Would you like to sort by price (P) or quantity (Q)?");
            choice = input.nextLine().trim();
            if (!choice.equalsIgnoreCase("P") && !choice.equalsIgnoreCase("Q")) {
                System.out.println("Invalid option. Please choose either P for price or Q for quantity.");
            }
        }

        String highToLowChoice = "";
        while (!highToLowChoice.equalsIgnoreCase("yes") && !highToLowChoice.equalsIgnoreCase("no")) {
            System.out.println("Sort high to low? (yes/no)");
            highToLowChoice = input.nextLine().trim();
            if (!highToLowChoice.equalsIgnoreCase("yes") && !highToLowChoice.equalsIgnoreCase("no")) {
                System.out.println("Invalid option. Please answer yes or no.");
            }
        }
        boolean highToLow = highToLowChoice.equalsIgnoreCase("yes");

        Comparator<Product> comparator;
        if (choice.equalsIgnoreCase("P")) {
            comparator = Comparator.comparingDouble(Product::getPrice);
        } else {
            comparator = Comparator.comparingInt(Product::getQuantityAvailable);
        }

        if (highToLow) {
            comparator = comparator.reversed();
        }

        products.sort(comparator);

        for (Product p : products) {
            System.out.println(p);
        }
    }
    public void exportPurchaseHistory() {
        File inputFile = new File(purchaseHistroyFile);
        File outputFile = new File(userName + "_PurchaseHistoryExport.csv");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            System.out.println("Purchase history exported successfully to " + outputFile.getName());
        } catch (IOException e) {
            System.out.println("Error while exporting purchase history: " + e.getMessage());
        }
    }

    public void viewDashboard(ArrayList<Product> products, Scanner input) {
        boolean cont = true;
        do {
            System.out.println("Select an option for dashboard view:");
            System.out.println("1. List of Stores by Number of Products Sold");
            System.out.println("2. List of Stores by Products Purchased By You");

            int choice = input.nextInt();
            switch (choice) {
                case 1 -> {
                    viewStoresByProductsSold(products);
                    cont = false;
                }
                case 2 -> {
                    viewStoresByYourPurchases();
                    cont = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        } while(cont);

    }

    private void viewStoresByProductsSold(ArrayList<Product> products) {
        ArrayList<StoreSales> storeSalesList = new ArrayList<>();

        for (Product product : products) {
            String storeName = product.getStore();
            int itemsSold = product.getItemsSold();

            StoreSales storeSales = findStoreSales(storeSalesList, storeName);
            if (storeSales == null) {
                storeSales = new StoreSales(storeName);
                storeSalesList.add(storeSales);
            }
            storeSales.addItemsSold(itemsSold);
        }

        // Sort by total items sold
        storeSalesList.sort((s1, s2) -> Integer.compare(s2.getTotalItemsSold(), s1.getTotalItemsSold()));

        // Display the results
        System.out.println("Stores by Number of Products Sold:");
        for (StoreSales sales : storeSalesList) {
            System.out.println("Store: " + sales.getStoreName() + ", Products Sold: " + sales.getTotalItemsSold());
        }
    }

    private StoreSales findStoreSales(List<StoreSales> list, String storeName) {
        for (StoreSales sales : list) {
            if (sales.getStoreName().equals(storeName)) {
                return sales;
            }
        }
        return null;
    }


    private void viewStoresByYourPurchases() {
        ArrayList<StorePurchaseData> storePurchases = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(purchaseHistroyFile))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                String storeName = values[3];

                StorePurchaseData existingData = findStoreData(storePurchases, storeName);
                if (existingData != null) {
                    existingData.incrementPurchaseCount();
                } else {
                    storePurchases.add(new StorePurchaseData(storeName));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        storePurchases.sort((a, b) -> b.getPurchaseCount() - a.getPurchaseCount());

        System.out.println("Stores by Your Purchases:");
        for (StorePurchaseData data : storePurchases) {
            System.out.println("Store: " + data.getStoreName() + ", Purchases: " + data.getPurchaseCount());
        }
    }

    private StorePurchaseData findStoreData(List<StorePurchaseData> list, String storeName) {
        for (StorePurchaseData data : list) {
            if (data.getStoreName().equals(storeName)) {
                return data;
            }
        }
        return null;
    }

    public static void main(String[] args) { //For Testing
        Consumer c = new Consumer("Sameer");
        Scanner input = new Scanner(System.in);
        ArrayList<Product> products;
        products = viewMarketPlace();
        c.addProduct(products.get(5));
        c.addProduct((products.get(3)));
        c.purchase();
    }
}
