import java.io.*;
import java.nio.file.*;
import java.util.*;

public class StoreplaceManager {
    private static String sellerName;
    private static String storeName;
    private static String productsFilePath = storeName + "_productsFilePath.csv";;
    private static String storeSalesFilePath = storeName + "_storeSalesFilePath.csv";

    private static final String CSV_FILE = "user_data.csv";
    private static final String PURCHASE_HISTORY_SUFFIX = "PurchaseHistory.csv";
    private static Map<String, Integer> itemsPurchasedMap = new HashMap<>();

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
            System.out.println("[4] Export Product");
            System.out.println("[5] View Sales");
            System.out.println("[6] Exit");

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
                        exportProduct(input);
                        break;
                    case 5:
                        calculateItemsPurchased();
                        break;
                    case 6:
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
            input.nextLine();
            System.out.print("Enter product name: ");
            String name = input.nextLine();
            System.out.print("Enter product description: ");
            String description = input.nextLine();
            System.out.print("Enter product price: ");
            String price = input.nextLine();
            try {
                doublePrice = Double.parseDouble(price);
            } catch (IllegalArgumentException e){
                System.out.println("Type in a double value");
            }

            System.out.print("Enter product quantity available: ");
            String quantityAvailable = input.nextLine();
            try {
                intQuantityAvailable = Integer.parseInt(price);
            } catch (IllegalArgumentException e){
                System.out.println("Type in a double value");
            }


            if (!Files.exists(Paths.get(productsFilePath))) {
                Files.createFile(Paths.get(productsFilePath));
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(productsFilePath, true))) {
                writer.write(sellerName + "," + name + "," + description + "," + doublePrice + "," + intQuantityAvailable);
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
            } catch (IllegalArgumentException e){
                System.out.println("Type in a double value");
            }
            System.out.print("Enter the new quantity available: ");
            String newQuantityAvailable = input.nextLine();
            try {
                intNewQuantityAvailable = Integer.parseInt(newQuantityAvailable);
            } catch (IllegalArgumentException e){
                System.out.println("Type in a double value");
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(productsFilePath));
                 BufferedWriter writer = new BufferedWriter(new FileWriter("productsFilePath.tmp"))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] productDetails = line.split(",");
                    if (productDetails.length == 5 && productDetails[1].equals(productName)) {
                        productDetails[2] = description;
                        productDetails[3] = Double.toString(newDoublePrice);
                        productDetails[4] = Integer.toString(intNewQuantityAvailable);
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

    private void exportProduct(Scanner input) {
        input.nextLine();
        System.out.println("Enter the file you wish to export: ");
        String filePath = input.nextLine();
        File inputFile = new File(filePath);
        File outputFile = new File(productsFilePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {

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

    public static void calculateItemsPurchased() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    String email = data[1].trim();
                    String role = data[2].trim();

                    if (role.equals("buyer")) {
                        calculateUserItemsPurchased(email);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        printItemsPurchasedStatistics();
    }

    private static void calculateUserItemsPurchased(String email) {
        String purchaseHistoryFile = email + PURCHASE_HISTORY_SUFFIX;
        int itemsPurchased = 0;

        try (BufferedReader historyReader = new BufferedReader(new FileReader(purchaseHistoryFile))) {
            // Assuming each line in the purchase history file corresponds to one item
            while (historyReader.readLine() != null) {
                itemsPurchased++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemsPurchasedMap.put(email, itemsPurchased);
    }

    private static void printItemsPurchasedStatistics() {
        System.out.println("Items Purchased Statistics:");
        for (Map.Entry<String, Integer> entry : itemsPurchasedMap.entrySet()) {
            System.out.println("User Email: " + entry.getKey());
            System.out.println("Items Purchased: " + entry.getValue());
            System.out.println("---------------");
        }
    }

    private static StoreSales findStoreSales(List<StoreSales> list, String storeName) {
        for (StoreSales sales : list) {
            if (sales.getStoreName().equals(storeName)) {
                return sales;
            }
        }
        return null;
    }


    private void replaceOriginalFile(String original, String tempfile) {
        try {
            Path originalPath = Paths.get(original);
            Path tempPath = Paths.get(tempfile);

            List<String> tempLines = Files.readAllLines(tempPath);
            Files.write(originalPath, tempLines, StandardOpenOption.TRUNCATE_EXISTING);

            Files.delete(tempPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
