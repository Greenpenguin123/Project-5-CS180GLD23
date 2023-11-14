import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import static java.lang.Integer.parseInt;

// fixed

public class SellerDashboard {
    public static String name = "";
    private static Map<String, StoreplaceManager> storeManagers = new HashMap<>();

    public SellerDashboard(String name) throws IOException {
        this.storeManagers = new HashMap<>();
        this.name = name;
        loadStoresFromFile(); // Load existing stores from file on initialization
    }

    public static void displayDashboard(String name) {
        System.out.println("Hello " + name);
        Scanner input = new Scanner(System.in);
        String choice;
        int intChoice = 0;

        do {
            System.out.println("Seller Dashboard:\nWhat would you like to do:");
            System.out.println("[1] Create Store");
            System.out.println("[2] Enter Store");
            System.out.println("[3] Exit");
            choice = input.nextLine();
            try {
                intChoice = parseInt(choice);
            } catch (NumberFormatException e) {
                System.out.println("Please type in an integer! ");
            }
            try {
                switch (intChoice) {
                    case 1:
                        createStore(input);
                        break;
                    case 2:
                        enterStore(input);
                        break;
                    case 3:
                        System.out.println("Exiting Seller Dashboard");
                        saveStoresToFile(); // Save store information to file before exiting
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Print out an integer.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (true);
    }

    private static void createStore(Scanner input) {
        System.out.print("Enter store name: ");
        String storeName = input.nextLine();
        if (!storeManagers.containsKey(storeName)) {
            StoreplaceManager storeManager = new StoreplaceManager(name, storeName);
            storeManagers.put(storeName, storeManager);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(name + "store_data.csv"))) {
                writer.write(storeName + ",");
                writer.newLine();
                System.out.println("Store '" + storeName + "' created successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Store with the same name already exists.");
        }
    }

    private static void enterStore(Scanner input) throws IOException {
        System.out.print("Enter store name: ");
        String storeName = input.nextLine().trim(); // Trim the user input

        StoreplaceManager storeManager;

        try (BufferedReader reader = new BufferedReader(new FileReader(name + "store_data.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] storeDetails = line.split(",");

                if (storeName.equals(storeDetails[0].trim())) {
                    storeManager = storeManagers.get(storeName);
                    if (storeManager != null) {
                        storeManager.displayDashboard(storeName);
                        return;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }

        System.out.println("Store not found. Please create the store first.");
    }

    private static void loadStoresFromFile() throws IOException {
        if (!Files.exists(Paths.get(name + "store_data.csv"))) {
            Files.createFile(Paths.get(name + "store_data.csv"));
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(name + "store_data.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] storeDetails = line.split(",");
                if (storeDetails.length == 2) {
                    String sellerName = storeDetails[0].trim();
                    String storeName = storeDetails[0].trim();
                    storeManagers.put(storeName, new StoreplaceManager(sellerName, storeName));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveStoresToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(name + "store_data.csv"))) {
            for (Map.Entry<String, StoreplaceManager> entry : storeManagers.entrySet()) {
                writer.write(name + "," + entry.getKey());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
