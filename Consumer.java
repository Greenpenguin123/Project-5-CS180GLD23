import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

public class Consumer {
    private String userName;
    private String cartFile;
    private String purchaseHistoryFile;
    private static ArrayList<Product> productList = getProductList();

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

    public ArrayList<Product> returnProductList() {
        return productList;
    }

    public static void viewMarketPlace() {
        ArrayList<Product> products = getProductList();
        String productListText = "";

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            productListText += (i + 1) + ". " + product.toString() + "\n";
        }

        JOptionPane.showMessageDialog(null, productListText, "Market Place", JOptionPane.INFORMATION_MESSAGE);
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



    public static void searchProducts(ArrayList<Product> products) {
        try {
            String keyword = JOptionPane.showInputDialog("What would you like to search?");
            String searchResults = "";

            for (Product product : products) {
                if (product.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        product.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                        product.getStore().toLowerCase().contains(keyword.toLowerCase())) {
                    searchResults += product.toString() + "\n";
                }
            }

            if (searchResults.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No products found matching your search.", "Search", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, searchResults, "Search Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Cancel or X Pressed",
                        "Search Product", JOptionPane.INFORMATION_MESSAGE);
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
            e.printStackTrace(); // Log the exception
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
        String history = "";
        String filePath = purchaseHistoryFile;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                history += lineNumber + ". " + line + "\n";
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while reading the file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null, history, "Purchase History", JOptionPane.INFORMATION_MESSAGE);
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
            updateMarketplaceFile();
            clearCart();
            JOptionPane.showMessageDialog(null, "Purchase Successful", "Purchase Product", JOptionPane.INFORMATION_MESSAGE);
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

    public void clearCart() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(cartFile))) {
            pw.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sortMarketPlaceGUI(ArrayList<Product> products) {
        // Ask the user to choose the sorting criteria
        String[] criteriaOptions = {"Price", "Quantity"};
        String criteriaChoice = (String) JOptionPane.showInputDialog(null,
                "Would you like to sort by price or quantity?",
                "Sort Criteria", JOptionPane.QUESTION_MESSAGE, null,
                criteriaOptions, criteriaOptions[0]);

        if (criteriaChoice == null) return; // User cancelled the operation

        // Ask the user to choose the sorting order
        String[] orderOptions = {"High to Low", "Low to High"};
        String orderChoice = (String) JOptionPane.showInputDialog(null,
                "Sort high to low?",
                "Sort Order", JOptionPane.QUESTION_MESSAGE, null,
                orderOptions, orderOptions[0]);

        if (orderChoice == null) return; // User cancelled the operation

        // Set up the comparator based on user choices
        Comparator<Product> comparator = criteriaChoice.equals("Price") ?
                Comparator.comparingDouble(Product::getPrice) :
                Comparator.comparingInt(Product::getQuantityAvailable);

        if (orderChoice.equals("High to Low")) {
            comparator = comparator.reversed();
        }

        // Sort the products
        products.sort(comparator);

        // Build a string to display sorted products
        StringBuilder sortedProducts = new StringBuilder();
        for (Product p : products) {
            sortedProducts.append(p.toString()).append("\n"); // Assuming Product's toString method gives the desired display format
        }

        // Show the sorted list in a dialog
        JTextArea textArea = new JTextArea(sortedProducts.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(null, scrollPane, "Sorted Products", JOptionPane.INFORMATION_MESSAGE);
    }

    public void exportPurchaseHistoryGUI() {
        File inputFile = new File(purchaseHistoryFile);
        File outputFile = new File(userName + "_PurchaseHistoryExport.csv");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            // Show a success message
            JOptionPane.showMessageDialog(null, "Purchase history exported successfully to " + outputFile.getName(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            // Show an error message
            JOptionPane.showMessageDialog(null, "Error while exporting purchase history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void viewDashboardGUI(ArrayList<Product> products) {
        boolean validInput = false;
        while (!validInput) {
            String optionsMessage = "Select an option for dashboard view:\n"
                    + "1. List of Stores by Number of Products Sold\n"
                    + "2. List of Stores by Products Purchased By You";

            String choice = JOptionPane.showInputDialog(null, optionsMessage, "Dashboard View", JOptionPane.QUESTION_MESSAGE);

            if (choice == null) {
                JOptionPane.showMessageDialog(null, "Dashboard view cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                break;
            }

            try {
                int selectedOption = Integer.parseInt(choice);
                StringBuilder message = new StringBuilder();
                validInput = true;

                switch (selectedOption) {
                    case 1 -> {
                        StoreplaceManager.calculateItemsPurchased();
                        ArrayList<StoreSales> storeSalesList = viewStoresByProductsSold(products);
                        for (StoreSales sales : storeSalesList) {
                            message.append("Store: ").append(sales.getStoreName()).append(", Products Sold: ").append(sales.getTotalItemsSold()).append("\n");
                        }
                        JOptionPane.showMessageDialog(null, message.toString(), "Stores by Number of Products Sold", JOptionPane.INFORMATION_MESSAGE);
                    }
                    case 2 -> {
                        ArrayList<StorePurchaseData> storePurchases = viewStoresByYourPurchases();
                        for (StorePurchaseData data : storePurchases) {
                            message.append("Store: ").append(data.getStoreName()).append(", Purchases: ").append(data.getPurchaseCount()).append("\n");
                        }
                        JOptionPane.showMessageDialog(null, message.toString(), "Stores by Your Purchases", JOptionPane.INFORMATION_MESSAGE);
                    }
                    default -> {
                        JOptionPane.showMessageDialog(null, "Invalid choice. Please select a valid option.", "Error", JOptionPane.ERROR_MESSAGE);
                        validInput = false;
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void sortDashboardGUI() {
        ArrayList<StoreSales> storeSalesList = viewStoresByProductsSold(productList);
        ArrayList<StorePurchaseData> storePurchases = viewStoresByYourPurchases();

        String[] options = {"Number of Products Sold (High-Low)", "Number of Products Sold (Low-High)",
                "Products Purchased by You (High-Low)", "Products Purchased by You (Low-High)"};
        String choice = (String) JOptionPane.showInputDialog(null, "How would you like to sort the dashboard?",
                "Sort Dashboard", JOptionPane.QUESTION_MESSAGE, null,
                options, options[0]);

        if (choice != null && !choice.isEmpty()) {
            StringBuilder message = new StringBuilder();
            switch (choice) {
                case "Number of Products Sold (High-Low)":
                    storeSalesList.sort((p1, p2) -> Integer.compare(p2.getTotalItemsSold(), p1.getTotalItemsSold()));
                    break;
                case "Number of Products Sold (Low-High)":
                    storeSalesList.sort(Comparator.comparingInt(StoreSales::getTotalItemsSold));
                    break;
                case "Products Purchased by You (High-Low)":
                    storePurchases.sort((p1, p2) -> Integer.compare(p2.getPurchaseCount(), p1.getPurchaseCount()));
                    break;
                case "Products Purchased by You (Low-High)":
                    storePurchases.sort(Comparator.comparingInt(StorePurchaseData::getPurchaseCount));
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice. Please select a valid option.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }

            // Display the sorted data
            if (choice.contains("Products Sold")) {
                for (StoreSales sales : storeSalesList) {
                    message.append("Store: ").append(sales.getStoreName()).append(", Products Sold: ").append(sales.getTotalItemsSold()).append("\n");
                }
            } else {
                for (StorePurchaseData data : storePurchases) {
                    message.append("Store: ").append(data.getStoreName()).append(", Purchases: ").append(data.getPurchaseCount()).append("\n");
                }
            }
            JOptionPane.showMessageDialog(null, message.toString(), "Sorted Dashboard", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No option selected.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
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

    public static void showDescriptionGUI(ArrayList<Product> productList) {
        String input = JOptionPane.showInputDialog("Enter the product number to view its description:");
        try {
            int selectedProduct = Integer.parseInt(input);
            int index = selectedProduct - 1;
            if (index < 0 || index >= productList.size()) {
                JOptionPane.showMessageDialog(null, "Invalid product number. Please select a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Product selected = productList.get(index);
            String productDescription = selected.getName() + "\n" +
                    selected.getDescription() + " ---- " +
                    selected.getQuantityAvailable() + " available";

            JOptionPane.showMessageDialog(null, productDescription, "Product Description", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            if (JOptionPane.CANCEL_OPTION == 2) {
                JOptionPane.showMessageDialog(null, "Operation cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void printCart() {
        ArrayList<String> cartItems = new ArrayList<>();
        String cartContents = "";

        try (BufferedReader br = new BufferedReader(new FileReader(cartFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                cartItems.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading cart file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Your cart is empty.", "Cart", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (int i = 0; i < cartItems.size(); i++) {
                cartContents += (i + 1) + ". " + cartItems.get(i) + "\n";
            }
            JOptionPane.showMessageDialog(null, cartContents, "Cart Items", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public ArrayList<String> getCartList() {
        ArrayList<String> cartItems = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(cartFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                cartItems.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cartItems;
    }

        public static void main(String[] args) { //For Testing
        Consumer c = new Consumer("Sameer");
        Scanner input = new Scanner(System.in);
        ArrayList<Product> products = getProductList();
        //viewMarketPlace();
        //c.printPurchaseHistory();
        //searchProducts(products);
        c.printCart();
        //c.addProduct(products.get(5));
    }
}