import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
/*
public class Consumer {
    private String userName;
    private String cartFile;
    private String purchaseHistoryFile;
    public static ArrayList<Product> productList = getProductList();

    public Consumer(String userName)  {
        this.userName = userName;
        this.cartFile = userName + "Cart.csv";
        this.purchaseHistoryFile = userName + "PurchaseHistory.csv";
        if (!Files.exists(Path.of(cartFile)) && !Files.exists(Path.of(purchaseHistoryFile))) {
            try {
                Files.createFile(Path.of(purchaseHistoryFile));
                Files.createFile(Path.of(cartFile));

            } catch (IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<Product> viewMarketPlace() {
        ArrayList<Product> products;
        products = getProductList();
        int i  = 1;
        for (Product product : products) {
            System.out.println(i + ". " + product);
            i++;
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

    public void removeProduct(String productName) { //removes every instance of that product
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
            return;
        }

        // overwrite the original file with the contents of the temp file
        try (BufferedReader tempReader = new BufferedReader(new FileReader(tempFile));
             BufferedWriter originalWriter = new BufferedWriter(new FileWriter(inputFile))) {

            String currentLine;
            while ((currentLine = tempReader.readLine()) != null) {
                originalWriter.write(currentLine + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not overwrite the original file with the temp file contents");
        }

        if (!tempFile.delete()) {
            System.out.println("Could not delete the temporary file");
        }
    }
    public void printPurchaseHistory() {
        String filePath = purchaseHistoryFile;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                System.out.println(lineNumber + ". " + line);
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while reading the file.");
        }
    }
    public void purchaseProduct() {
        boolean purchaseSuccessful = true;

        try (BufferedReader br = new BufferedReader(new FileReader(cartFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(purchaseHistoryFile, true))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] productDetails = line.split(",");
                if (productDetails.length >= 5) {
                    String name = productDetails[0];
                    String description = productDetails[1];
                    String store = productDetails[2];

                    Product product = findProduct(name, description, store);
                    if (product != null && product.getQuantityAvailable() >= 1) {
                        product.setQuantityAvailable(product.getQuantityAvailable() - 1);
                        recordPurchaseHistory(product, 1);
                    } else {
                        System.out.println("Product not available or insufficient quantity for: " + name);
                        purchaseSuccessful = false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            purchaseSuccessful = false;
        }

        if (purchaseSuccessful) {
            System.out.println("Thank You For Purchasing!");
            updateMarketplaceFile();
            clearCart(); // Clear the cart after successful purchase
        } else {
            System.out.println("Purchase partially successful or failed.");
        }
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
            }
        } catch (IOException e) {
            System.err.println("Error while updating the marketplace file: " + e.getMessage());
        }
    }

    private void recordPurchaseHistory(Product product, int quantity) {
        String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(purchaseHistoryFile, true))) {
            bw.write(timeStamp + "," + product.getName() + "," + product.getDescription() + "," + product.getStore() + "," + product.getPrice() + "," + quantity + "\n");
        } catch (IOException e) {
            System.err.println("Error while recording purchase history: " + e.getMessage());
        }
    }
    private Product findProduct(String name, String description, String store) {
        for (Product product : productList) {
            if (product.getName().equals(name) &&
                    product.getDescription().equals(description) &&
                    product.getStore().equals(store)) {
                return product;
            }
        }
        return null;
    }


    private void replaceMarketplaceFileWithTemp(File tempFile) {
        File marketplaceFile = new File("MarketPlace.csv");
        if (tempFile.renameTo(marketplaceFile)) {
            System.out.println("Marketplace file updated successfully.");
        } else {
            System.out.println("Failed to update Marketplace file.");
        }
    }


    public void clearCart() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(cartFile))) {
            pw.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateProductQuantity(Product purchasedProduct, int quantity) {
        purchasedProduct.setQuantityAvailable(purchasedProduct.getQuantityAvailable() - quantity);
    }
    public static void sortMarketPlace(ArrayList<Product> products, Scanner input) {
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
            System.out.println(p.printForSort());
        }
    }
    public void exportPurchaseHistory() {
        File inputFile = new File(purchaseHistoryFile);
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
        ArrayList<StoreSales> storeSalesList;
        ArrayList<StorePurchaseData> storePurchases;
        boolean cont = true;

        do {
            System.out.println("\nSelect an option for dashboard view:");
            System.out.println("1. List of Stores by Number of Products Sold");
            System.out.println("2. List of Stores by Products Purchased By You");

            if (input.hasNextInt()) {
                int choice = input.nextInt();
                input.nextLine();

                switch (choice) {
                    case 1 -> {
                        StoreplaceManager.calculateItemsPurchased();
                        /*storeSalesList = viewStoresByProductsSold(products);
                        // Display the results
                        System.out.println("Stores by Number of Products Sold:");

                        for (StoreSales sales : storeSalesList) {
                            System.out.println("Store: " + sales.getStoreName() + ", Products Sold: " + sales.getTotalItemsSold());
                        }
                        cont = false;
                    }
                    case 2 -> {
                        storePurchases = viewStoresByYourPurchases();
                        System.out.println("Stores by Your Purchases:");
                        assert storePurchases != null;
                        for (StorePurchaseData data : storePurchases) {
                            System.out.println("Store: " + data.getStoreName() + ", Purchases: " + data.getPurchaseCount());
                        }
                        cont = false;
                    }
                    default -> System.out.println("Invalid choice. Please select a valid option.");
                }
            } else {
                System.out.println("Please enter a valid number.");
                input.nextLine();
            }
        } while (cont);
    }


    public void sortDashboard(Scanner input) {
        ArrayList<StoreSales> storeSalesList = viewStoresByProductsSold(productList);
        ArrayList<StorePurchaseData> storePurchases = viewStoresByYourPurchases();
        boolean validInputReceived = false;

        while (!validInputReceived) {
            try {
                System.out.println("\nHow would you like to sort the dashboard?");
                System.out.println("1: Number of Products Sold (High-Low)");
                System.out.println("2: Number of Products Sold (Low-High)");
                System.out.println("3: Products Purchased by You (High-Low)");
                System.out.println("4: Products Purchased by You (Low-High)");

                int choice = input.nextInt();

                switch (choice) {
                    case 1 -> {
                        storeSalesList.sort((p1, p2) -> Integer.compare(p2.getTotalItemsSold(), p1.getTotalItemsSold()));
                        validInputReceived = true;
                    }
                    case 2 -> {
                        storeSalesList.sort(Comparator.comparingInt(StoreSales::getTotalItemsSold));
                        validInputReceived = true;
                    }
                    case 3 -> {
                        assert storePurchases != null;
                        storePurchases.sort((p1, p2) -> Integer.compare(p2.getPurchaseCount(), p1.getPurchaseCount()));
                        validInputReceived = true;
                    }
                    case 4 -> {
                        assert storePurchases != null;
                        storePurchases.sort(Comparator.comparingInt(StorePurchaseData::getPurchaseCount));
                        validInputReceived = true;
                    }
                    default -> System.out.println("Invalid choice. Please select a valid option.");
                }

                if (validInputReceived) {
                    if (choice <= 2) {
                        for (StoreSales sales : storeSalesList) {
                            System.out.println("Store: " + sales.getStoreName() + ", Products Sold: " + sales.getTotalItemsSold());
                        }
                    } else {
                        for (StorePurchaseData data : storePurchases) {
                            System.out.println("Store: " + data.getStoreName() + ", Purchases: " + data.getPurchaseCount());
                        }
                    }
                }

            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                input.nextLine();
            }
        }
    }

    private ArrayList<StoreSales> viewStoresByProductsSold(ArrayList<Product> products) {
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

        return storeSalesList;
    }

    private static StoreSales findStoreSales (ArrayList<StoreSales> list, String storeName) {
        for (StoreSales sales : list) {
            if (sales.getStoreName().equals(storeName)) {
                return sales;
            }
        }
        return null;
    }


    private ArrayList<StorePurchaseData> viewStoresByYourPurchases() {
        ArrayList<StorePurchaseData> storePurchases = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(purchaseHistoryFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                String storeName = values[3].trim(); // Trim to remove any leading/trailing spaces

                StorePurchaseData existingData = findStoreData(storePurchases, storeName);
                if (existingData != null) {
                    existingData.incrementPurchaseCount();
                } else {
                    storePurchases.add(new StorePurchaseData(storeName)); // Assuming 1 purchase for new store
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return storePurchases;
    }
    private static StorePurchaseData findStoreData(ArrayList<StorePurchaseData> list, String storeName) {
        for (StorePurchaseData data : list) {
            if (data.getStoreName().equals(storeName)) {
                return data;
            }
        }
        return null;
    }

    public static void showDescription(int selectedProduct, Scanner scanner) {
        boolean cont = true;
        do {
            try {
                int index = selectedProduct - 1;
                Product selected = productList.get(index);

                System.out.println(selected.getName());
                System.out.println(selected.getDescription() + " ----- " + selected.getQuantityAvailable() + " available");

                cont = false;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Select a valid input, please.");
                while (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next();
                }
                selectedProduct = scanner.nextInt();
                scanner.nextLine();
            }
        } while (cont);
    }
    public ArrayList<String> printCart() {
        ArrayList<String> cartItems = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(cartFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                cartItems.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print
        for (int i = 0; i < cartItems.size(); i++) {
            System.out.println((i + 1) + ". " + cartItems.get(i));
        }

        return cartItems;
    }

    public static void main(String[] args) { //For Testing
        Consumer c = new Consumer("Sameer");
        Scanner input = new Scanner(System.in);
        ArrayList<Product> products;
        products = viewMarketPlace();
        //c.addProduct(products.get(5));
        //c.addProduct((products.get(3)));
        //c.purchaseProduct();
        //viewDashboard(products, input);
        //sortDashboard(input);
        //c.addProduct(productList.get(5));
        //c.addProduct(productList.get(0));
        //c.removeProduct(productList.get(0).getName());
    }
}*/