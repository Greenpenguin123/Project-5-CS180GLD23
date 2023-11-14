import java.io.*;
import java.nio.file.*;
import java.util.*;

// not fixed

public class StoreplaceManager {
    private static String sellerName;
    private static int counter = 0;
    private static String storeName;
    private static String productsFilePath = "MarketPlace.csv";

    public StoreplaceManager(String inSellerName, String inStoreName) {
        this.sellerName = inSellerName;
        this.storeName = inStoreName;
    }

    public void displayDashboard(String storeName) {
        String choice = "";
        int intChoice = 0;
        Scanner input = new Scanner(System.in);

        do {
            System.out.println("Storeplace Manager Dashboard for " + StoreplaceManager.storeName);
            System.out.println("[1] Create Product");
            System.out.println("[2] Edit Product");
            System.out.println("[3] Delete Product");
            System.out.println("[4] Import Product");
            System.out.println("[5] Export Product");
            System.out.println("[6] View each customer's number of purchase; ");
            System.out.println("[7] View the list of products with their sale");
            System.out.println("[8] Exit");

            System.out.println("Enter your choice: ");
            choice = input.nextLine();
            try {
                intChoice = Integer.parseInt(choice);
            } catch (IllegalArgumentException h) {
                System.out.println("Type an integer in.");
            }

            try {
                switch (intChoice) {
                    case 1:
                        createProduct(input);
                        break;
                    case 2:
                        editProduct(input);
                        break;
                    case 3:
                        deleteProduct(input);
                        break;
                    case 4:
                        importProduct(input);
                        break;
                    case 5:
                        exportProduct(input);
                        break;
                    case 6:
                        printItemsPurchasedStatistics();
                        break;
                    case 7:
                        calculateProductSales();
                        break;
                    case 8:
                        System.out.println("Exiting Storeplace Manager Dashboard");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Print out an integer.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (true);
    }

    private void createProduct(Scanner input) {
        double doublePrice = 0.0;
        int intQuantityAvailable = 0;
        try {
            System.out.print("Enter product name: ");
            String name = input.nextLine();
            System.out.print("Enter product description: ");
            String description = input.nextLine();
            System.out.print("Enter product price: ");
            String price = input.nextLine();
            try {
                doublePrice = Double.parseDouble(price);
            } catch (IllegalArgumentException e) {
                System.out.println("Type in a double value");
            }

            System.out.print("Enter product quantity available: ");
            String quantityAvailable = input.nextLine();
            try {
                intQuantityAvailable = Integer.parseInt(price);
            } catch (IllegalArgumentException e) {
                System.out.println("Type in a double value");
            }


            if (!Files.exists(Paths.get(productsFilePath))) {
                Files.createFile(Paths.get(productsFilePath));
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(productsFilePath, true))) {
                writer.write(name + "," + description + "," + storeName + "," + doublePrice + "," + intQuantityAvailable);
                writer.newLine();
                System.out.println("Product created successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(productsFilePath, true))) {
                writer.write(name + "," + description + "," + doublePrice + "," + intQuantityAvailable);
                writer.newLine();
                System.out.println("Product created successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (InputMismatchException e) {
            throw new IllegalArgumentException("Invalid input. Please enter valid data.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void editProduct(Scanner input) {
        double newDoublePrice = 0.0;
        int intNewQuantityAvailable = 0;
        try {
            input.nextLine();
            System.out.print("Enter the name of the product to edit: ");
            String productName = input.nextLine();
            System.out.print("Enter a short description of the product: ");
            String description = input.nextLine();
            System.out.print("Enter the new price: ");
            String newPrice = input.nextLine();
            try {
                newDoublePrice = Double.parseDouble(newPrice);
            } catch (IllegalArgumentException e) {
                System.out.println("Type in a double value");
            }
            System.out.print("Enter the new quantity available: ");
            String newQuantityAvailable = input.nextLine();
            try {
                intNewQuantityAvailable = Integer.parseInt(newQuantityAvailable);
            } catch (IllegalArgumentException e) {
                System.out.println("Type in a double value");
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(productsFilePath));
                 BufferedWriter writer = new BufferedWriter(new FileWriter("productsFilePath.tmp"))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] productDetails = line.split(",");
                    if (productDetails.length == 5 && productDetails[1].equals(productName)) {
                        productDetails[2] = description;
                        productDetails[3] = storeName;
                        productDetails[4] = Double.toString(newDoublePrice);
                        productDetails[5] = Integer.toString(intNewQuantityAvailable);
                    }
                    writer.write(String.join(",", productDetails));
                    writer.newLine();
                }

                // Ensure that resources are closed before moving the file
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Files.move(Paths.get("productsFilePath.tmp"), Paths.get(productsFilePath), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Product edited successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (InputMismatchException e) {
            throw new IllegalArgumentException("Invalid input. Please enter valid data.");
        }
    }

    private void deleteProduct(Scanner input) throws IOException {
        Boolean deleted = false;
        input.nextLine();
        System.out.print("Enter the name of the product to delete: ");
        String productName = input.nextLine();
        if (!Files.exists(Paths.get(productsFilePath))) {
            Files.createFile(Paths.get(productsFilePath));
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(productsFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter("productsFilePath.tmp"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] productDetails = line.split(",");
                if (productDetails.length == 5 && productDetails[1].equals(productName)) {
                    deleted = true;
                    continue;  // Skip the line for the deleted product
                } else {
                    writer.write(String.join(",", productDetails));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (deleted) {
            replaceOriginalFile(productsFilePath, "productsFilePath.tmp");
            System.out.println("Product deleted successfully.");
        } else {
            System.out.println("Product wasn't found");
        }
    }

    private void importProduct(Scanner input) {
        System.out.println("Enter the file you wish to import: ");
        String filePath = input.nextLine();
        File inputFile = new File(filePath);
        File outputFile = new File("MarketPlace.csv");
        if (Files.exists(Paths.get(inputFile.toURI()))) {
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }

                System.out.println("Purchase history imported successfully to " + outputFile.getName());
            } catch (IOException e) {
                System.out.println("Error while importing purchase history: " + e.getMessage());
            }
        } else {
            System.out.println("File not found! ");
        }
    }
    private void exportProduct(Scanner input) {
        System.out.println("Enter the file you wish to export: ");
        String filePath = input.nextLine();
        File inputFile = new File(filePath);
        File outputFile = new File("ExportFile" + counter + ".csv");

        if (Files.exists(Paths.get(inputFile.toURI()))) {
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }

                System.out.println("Data exported successfully to " + outputFile.getName());
                counter++; // Increment the counter for the next export

            } catch (IOException e) {
                System.out.println("Error while exporting data: " + e.getMessage());
            }
        } else {
            System.out.println("File not found! ");
        }
    }

    public static void printItemsPurchasedStatistics() {
        try (BufferedReader userDataReader = new BufferedReader(new FileReader("user_data.csv"))) {
            String userLine;
            while ((userLine = userDataReader.readLine()) != null) {
                String[] userData = userLine.split(",");
                if (userData.length == 3) {
                    String userId = userData[1];
                    String userEmail = userData[0];

                    int itemsPurchased = calculateItemsPurchased(userId);

                    System.out.println("User email: " + userEmail);
                    System.out.println("Items purchased: " + itemsPurchased);
                    System.out.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int calculateItemsPurchased(String userId) {
        int itemsPurchased = 0;
        String purchaseHistoryFileName = userId + "PurchaseHistory.csv";

        try (BufferedReader purchaseHistoryReader = new BufferedReader(new FileReader(purchaseHistoryFileName))) {
            String purchaseHistoryLine;
            while ((purchaseHistoryLine = purchaseHistoryReader.readLine()) != null) {
                String[] purchaseDetails = purchaseHistoryLine.split(",");
                if (purchaseDetails.length == 6) {
                    int quantity = Integer.parseInt(purchaseDetails[5]);
                    itemsPurchased += quantity;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return itemsPurchased;
    }



    public static void calculateStoreSales() {
        Map<String, Integer> storeSales = new HashMap<>();

        try (BufferedReader marketplaceReader = new BufferedReader(new FileReader("MarketPlace.csv"));
             BufferedWriter storeSalesWriter = new BufferedWriter(new FileWriter("storeSales.csv"))) {

            // Read the third element of each row in MarketPlace.csv and add unique stores to storeSales
            String marketplaceLine;
            while ((marketplaceLine = marketplaceReader.readLine()) != null) {
                String[] productDetails = marketplaceLine.split(",");
                if (productDetails.length == 5) {
                    String storeName = productDetails[2];

                    // Add unique stores to storeSales
                    if (!storeSales.containsKey(storeName)) {
                        storeSales.put(storeName, 0);
                    }
                }
            }

            // Write store names to storeSales.csv
            for (String storeName : storeSales.keySet()) {
                storeSalesWriter.write(storeName + "," + storeSales.get(storeName) + "\n");
            }

            // Go through the file again and update quantities in storeSales.csv
            marketplaceReader.close();
            BufferedReader marketReader = new BufferedReader(new FileReader("MarketPlace.csv"));

            while ((marketplaceLine = marketReader.readLine()) != null) {
                String[] productDetails = marketplaceLine.split(",");
                if (productDetails.length == 5) {
                    String storeName = productDetails[2];
                    int quantity = Integer.parseInt(productDetails[4]);

                    // Update quantities in storeSales.csv
                    if (storeSales.containsKey(storeName)) {
                        int currentSales = storeSales.get(storeName);
                        storeSales.put(storeName, currentSales + quantity);
                    }
                }
            }

            // Display store sales
            System.out.println("Store Sales:");
            for (Map.Entry<String, Integer> entry : storeSales.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + " products sold");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void calculateProductSales() {
        Map<String, Integer> productSales = new HashMap<>();

        // Write products from MarketPlace.csv to productSales.csv
        try (BufferedReader marketplaceReader = new BufferedReader(new FileReader("MarketPlace.csv"));
             BufferedWriter productSalesWriter = new BufferedWriter(new FileWriter("productSales.csv"))) {

            String marketplaceLine;
            while ((marketplaceLine = marketplaceReader.readLine()) != null) {
                String[] productDetails = marketplaceLine.split(",");
                if (productDetails.length == 5) {
                    String productName = productDetails[0];
                    productSales.put(productName, 0); // Initialize counts to 0
                    productSalesWriter.write(productName + "," + 0 + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Process user purchase history and update productSales
        try (BufferedReader userDataReader = new BufferedReader(new FileReader("user_data.csv"))) {
            // Read user data and create a map of user IDs to user names
            Map<String, String> userIdToName = new HashMap<>();
            String userLine;
            while ((userLine = userDataReader.readLine()) != null) {
                String[] userData = userLine.split(",");
                if (userData.length == 3) {
                    userIdToName.put(userData[0], userData[1]);  // Second element is the user ID, first is the user name
                }
            }

            // Iterate through each user's purchase history and update productSales
            for (String userId : userIdToName.keySet()) {
                String purchaseHistoryFileName = userIdToName.get(userId) + "PurchaseHistory.csv";
                Path purchaseHistoryPath = Paths.get(purchaseHistoryFileName);

                if (Files.exists(purchaseHistoryPath) && Files.size(purchaseHistoryPath) > 0) {
                    try (BufferedReader purchaseHistoryReader = new BufferedReader(new FileReader(purchaseHistoryFileName))) {
                        String purchaseHistoryLine;
                        while ((purchaseHistoryLine = purchaseHistoryReader.readLine()) != null) {
                            String[] purchaseDetails = purchaseHistoryLine.split(",");
                            if (purchaseDetails.length == 6) {
                                String productName = purchaseDetails[1];
                                int quantity = Integer.parseInt(purchaseDetails[5]);

                                // Update product sales count
                                int currentSales = productSales.get(productName);
                                productSales.put(productName, currentSales + quantity);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print the updated sales data
        for (Map.Entry<String, Integer> entry : productSales.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }


    private void replaceOriginalFile(String originalFile, String tempFile) {
        try {
            Path originalPath = Paths.get(originalFile);
            Path tempPath = Paths.get(tempFile);

            List<String> tempLines = Files.readAllLines(tempPath);
            Files.write(originalPath, tempLines, StandardOpenOption.TRUNCATE_EXISTING);

            Files.delete(tempPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

