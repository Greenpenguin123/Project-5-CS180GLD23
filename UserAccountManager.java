import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class UserAccountManager {

    private static final String CSV_FILE = "user_data.csv";
    private String email = null;
    private String role = null;
    private Boolean signedIn = false;
    private final int MAX_PASSWORD_LENGTH = 20;
    private final Logger LOGGER = Logger.getLogger(UserAccountManager.class.getName());
    private User currentUser; // Keep track of the current logged-in user
    public static String userName;

    public static void main(String[] args) throws IOException {
        UserAccountManager getInfo = new UserAccountManager();
        getInfo.run();
    }

    public String run() {
        Scanner input = new Scanner(System.in);
        setupLogger();
        int action = 0;

        do {
            System.out.println("Hello, welcome to the P4 Marketplace! Please Sign-in or Create an Account to continue.");
            System.out.println("Do you want to: \n[1] Create an account\n[2] Sign in");
            System.out.println("[3] Delete your account\n[4] Logout");
            System.out.println("[5] Cancel");
            String actionString = input.nextLine();

            try {
                action = parseInt(actionString);
            } catch (NumberFormatException e) {
                System.out.println("Please type in an integer! ");
            }

            try {
                switch (action) {
                    case 1:
                        gatherCreate(input);
                        break;
                    case 2:
                        gatherSignIn(input);
                        if (signedIn == true) {
                            System.out.println("Welcome " + role + ": " + email);
                            userName = getUserName("Welcome " + role + ": " + email);
                            if (role.equals("seller")) {
                                String sellerName = email.substring(0, email.indexOf("@"));
                                SellerDashboard seller;
                                seller = new SellerDashboard(sellerName);
                                SellerDashboard.displayDashboard(sellerName);
                            } else {
                                UserNavigation.main(null);
                            }
                            return"";
                        }
                        break;
                    case 3:
                        deleteUser(input);
                        break;
                    case 4:
                        logout();
                        break;
                    case 5:
                        System.out.println( "Goodbye, see you next time! ");
                        return"";
                    default:
                        System.out.println("Invalid choice. Please choose 'create', 'signin', 'delete', 'logout', or 'cancel'. \n");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Please type in an integer!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (!signedIn);
        return "";
    }

    public void gatherCreate(Scanner input) {
        String message = "User Created -";
        String result;
        String password;

        while (true) {
            do {
                System.out.print("Are you a seller or buyer (buyer/seller): ");
                role = input.nextLine();
            } while (!role.equals("buyer") && !role.equals("seller"));

            System.out.print("Enter your email: ");
            email = input.nextLine();

            System.out.print("Create a password: ");
            password = input.nextLine();

            try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    String storedEmail = data[0].trim();
                    String storedRole = data[2].trim();

                    if (storedEmail.equals(email) && storedRole.equals(role)) {
                        result = "Failed";
                        System.out.println("A user with that email has been registered! ");
                        log(message, result);
                        System.out.println();
                        return;
                    }
                }
                if (email.isBlank() || password.isBlank()) {
                    System.out.println("Invalid input. Email and password cannot be blank.");
                    System.out.print("---------------");

                } else {
                    createUser(email, password, role);
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void gatherSignIn(Scanner input) {
        String password;

        while (true) {
            do {
                System.out.print("Are you a seller or buyer (buyer/seller): ");
                role = input.nextLine();
            } while (!role.equals("buyer") && !role.equals("seller"));

            System.out.print("Enter your email: ");
            email = input.nextLine();

            System.out.print("Enter your password: ");
            password = input.nextLine();

            if (!email.isBlank() && !password.isBlank() && !role.isBlank()) {
                break;
            }
        }
        User user = loginUser(email, password);
        if (currentUser != null) {
            System.out.println("Another user logged in: " + user.getEmail());
        } else if (user != null) {
            System.out.println("User logged in: " + user.getEmail());
            currentUser = user; // Set the current user upon successful login
            signedIn = true;
        } else {
            System.out.println("Login failed. Invalid username or password.\n");
        }
    }

    private void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("user_operations.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String message, String result) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        LOGGER.log(Level.INFO, timestamp + " - " + message + " - " + result);
    }

    private void saveUserToCSV(String email, String password, String buyerSellerRole) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(CSV_FILE, true)))) {
            writer.println(email + "," + password + "," + buyerSellerRole);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User loginUser(String email, String password) {
        String message = "User login attempt";
        String result = "Failure";
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) { // Check if the array has at least two elements
                    String storedEmail = data[0].trim();
                    String storedPassword = data[1].trim();

                    if (storedEmail.equals(email) && storedPassword.equals(password) && currentUser == null) {
                        message = "User login successful!";
                        result = "Success";
                        log(message, result);
                        return new User(password, email, data[2].charAt(0), data[2].charAt(0));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log(message, result);
        return null;
    }


    private void createUser(String email, String password, String role) {
        String message = "User created!";
        String result = "Success";

        if (isPasswordValid(password) && isValidEmail(email)) {
            saveUserToCSV(email, password, role);
            System.out.println(message);
            log(message, result);
        } else {
            result = "Failed";
            System.out.println("Invalid user information. User not created.");
            log(message, result);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() <= MAX_PASSWORD_LENGTH;
    }

    private boolean isValidEmail(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void deleteUser(Scanner input) {
        String password = "";
        String message = "User Deletion Attempt";
        String result = "Failed";
        String TEMP_FILE = "TEMP.csv";

        while (true) {
            do {
                System.out.print("Are you a seller or buyer (buyer/seller): ");
                role = input.nextLine();
            } while (!role.equals("buyer") && !role.equals("seller"));

            System.out.print("Enter your email: ");
            email = input.nextLine();

            System.out.print("Enter your password: ");
            password = input.nextLine();

            if (!email.isBlank() && !password.isBlank() && !role.isBlank()) {
                break;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE))) {

            String line;
            boolean userFound = false;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    String storedEmail = data[0].trim();
                    String storedPassword = data[1].trim();

                    if (storedEmail.equals(email) && storedPassword.equals(password) && data[2].trim().equals(role)) {
                        userFound = true;
                        System.out.println("User found and deleted successfully.");
                        result = "Success";
                    } else {
                        writer.write(line + "\n");
                    }
                }
            }

            if (!userFound) {
                System.out.println("User not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String TEMP_File = "TEMP.csv";
        replaceOriginalFile(TEMP_File);
        log(message, result);


    }

    private static void replaceOriginalFile(String tempfile) {
        try {
            Path originalPath = Paths.get(CSV_FILE);
            Path tempPath = Paths.get(tempfile);

            List<String> tempLines = Files.readAllLines(tempPath);
            Files.write(originalPath, tempLines, StandardOpenOption.TRUNCATE_EXISTING);

            Files.delete(tempPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        if (currentUser == null) {
            System.out.println("No user is currently logged in. Logout not allowed.");
        } else {
            System.out.println("User logged out: " + currentUser.getEmail());
            currentUser = null; // Reset the current user
        }
    }
    public static String getUserName(String welcomeMessage) {
        String[] parts = welcomeMessage.split(": ");
        String email = parts[1];
        return email.substring(0, email.indexOf("@"));
    }

}
