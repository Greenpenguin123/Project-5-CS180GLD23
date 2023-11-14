import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserAccountManager {

    private final String CSV_FILE = "user_data.csv";
    private final int MAX_PASSWORD_LENGTH = 20;
    private final Logger LOGGER = Logger.getLogger(UserAccountManager.class.getName());
    private User currentUser; // Keep track of the current logged-in user


    public static void main(String[] args) throws IOException {
        UserAccountManager getInfo = new UserAccountManager();
        getInfo.run();
    }

    public void run() {
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome! \n");
        setupLogger();

        do {
            System.out.println("Do you want to: \n[1] Create an account\n[2] Sign in");
            System.out.println("[3] Delete your account\n[4] Logout");
            System.out.println("[5] Cancel");

            int action = input.nextInt();

            switch (action) {
                case 1:
                    gatherCreate(input);
                    break;
                case 2:
                    gatherSignIn(input);
                    break;
                case 3:
                    deleteUser(input);
                    break;
                case 4:
                    logout(); // Added logout option
                    break;
                case 5:
                    System.out.println("Goodbye, see you next time! ");
                    return;
                default:
                    System.out.println("Invalid choice. Please choose 'create', 'signin', 'delete', 'logout', or 'cancel'. \n");
            }
        } while (true);
    }

    public void gatherCreate(Scanner input) {
        String role, email, password;

        while (true) {
            input.nextLine();

            do {
                System.out.print("Are you a seller or buyer (buyer/seller): ");
                role = input.nextLine();
            } while (!role.equals("buyer") && !role.equals("seller"));

            System.out.print("Enter your email: ");
            email = input.nextLine();

            System.out.print("Create a password: ");
            password = input.nextLine();

            if (email.isBlank() || password.isBlank()) {
                System.out.println("Invalid input. Email and password cannot be blank.");
                System.out.print("---------------");
            } else {
                createUser(email, password, role);
                break;
            }
        }

    }

    public void gatherSignIn(Scanner input) {
        String role, email, password;

        while (true) {
            input.nextLine();
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
                String storedEmail = data[0].trim();
                String storedPassword = data[1].trim();

                if (storedEmail.equals(email) && storedPassword.equals(password) && currentUser != null) {
                    message = "User login successful!";
                    result = "Success";
                    log(message, result);
                    return new User(password, email, data[2].charAt(0), data[2].charAt(0));
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
            result = "Failure";
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

    private boolean deleteUser(Scanner input) {
        String message = "User deletion attempt";
        String result = "";
        String role;
        String email;
        String password;

        while (true) {
            input.nextLine();
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
             BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String storedEmail = data[0].trim();
                String storedPassword = data[1].trim();

                if (storedEmail.equals(email) && storedPassword.equals(password) && data[2].trim().equals(role)) {
                    result = "Success";
                    break;
                } else {
                    writer.write(line + "\n");
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Error processing file
        }

        log(message, result);
        return false;
    }
    public void logout() {
        if (currentUser == null) {
            System.out.println("No user is currently logged in. Logout not allowed.");
        } else {
            currentUser.logout(); // Invoke the logout method in the User class
            System.out.println("User logged out: " + currentUser.getEmail());
            currentUser = null; // Reset the current user
        }
    }

}
