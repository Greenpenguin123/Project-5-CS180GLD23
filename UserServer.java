import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UserServer {
    private static final Logger LOGGER = Logger.getLogger(UserServer.class.getName());
    private static final int PORT = 12345;
    private Map<String, String> userCredentials; // Map to store user credentials (email -> password)
    private Map<String, String> userRoles;       // Map to store user roles (email -> role)

    private static final String USER_DATA_FILE = "user_data.csv";

    public UserServer() throws IOException {
        userCredentials = new HashMap<>();
        userRoles = new HashMap<>();
        loadUserDataFromCSV(USER_DATA_FILE);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handle client connection in a separate thread
                new Thread(() -> {
                    String role = handleClient(clientSocket);
                    if ("buyer".equalsIgnoreCase(role)) {
                        launchBuyerProcess(clientSocket);
                    } else if ("seller".equalsIgnoreCase(role)) {
                        launchSellerProcess(clientSocket);
                    } else {
                        System.out.println("Unknown role: " + role);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserDataFromCSV(String csvFileName) throws IOException {
        if (!Files.exists(Paths.get(csvFileName))) {
            Files.createFile(Paths.get(csvFileName));
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {
                    String email = data[0].trim();
                    String password = data[1].trim();
                    String role = data[2].trim();

                    userCredentials.put(email, password);
                    userRoles.put(email, role);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeUserDataToCSV(String email, String password, String role) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE, true))) {
            writer.write(String.format("%s,%s,%s%n", email, password, role));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request = reader.readLine();
            String email = reader.readLine();
            String password = reader.readLine();
            String role = reader.readLine(); // Read the role from the client

            if ("CREATE".equals(request)) {
                return handleCreateRequest(email, password, role, writer);
            } else if ("LOGIN".equals(request)) {
                return handleLoginRequest(email, password, role, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String handleCreateRequest(String email, String password, String role, PrintWriter writer) {
        String response;
        if (!isValidEmail(email)) {
            response = "INVALID_EMAIL";
        } else if (password.length() <= 20) {
            response = "INVALID_PASSWORD_LENGTH";
        } else if (userRoles.containsKey(email) && userRoles.containsKey(role)) {
            response = "EMAIL_ALREADY_EXISTS_FOR_THAT_ROLE";
        } else {
            // Valid email and password, create user
            userCredentials.put(email, password);
            userRoles.put(email, role);

            // Write user data to CSV
            writeUserDataToCSV(email, password, role);

            response = "SUCCESS";

        }
        writer.println(response);
        if (response.equals("SUCCESS")) {
            return email + ", " + role;
        }
        return null;
    }

    private String handleLoginRequest(String email, String password, String role, PrintWriter writer) {
        String response;
        if (!isValidEmail(email)) {
            response = "INVALID_EMAIL";
        } else if (!userCredentials.containsKey(email) || !userCredentials.get(email).equals(password)) {
            response = "INVALID_CREDENTIALS";

        } else if (!userRoles.containsKey(email) || !userRoles.get(email).equals(role)) {
            response = "INVALID_CREDENTIALS_FOR_ROLE";
        } else {
            // Valid login credentials
            response = "SUCCESS";
        }
        writer.println(response);
        if (response.equals("SUCCESS")) {
            return "email" + ", " + role;
        }
        return null;
    }

    private boolean isValidEmail(String email) {
        String emailDomain = "";
        if (email.contains("@")) {
            if (email.indexOf("@") < email.lastIndexOf(".")) {
                emailDomain = email.substring(email.indexOf("."), email.length());
                if (emailDomain.length() >= 2 && emailDomain.length() <= 4 ) {
                    return true;
                }
            }
        }
        return false;

    }

    private void launchBuyerProcess(Socket clientSocket) {
        // Implement the buyer process here
        System.out.println("Launching Buyer Process for " + clientSocket.getInetAddress());
        // Add your buyer-specific logic and process code
    }

    private void launchSellerProcess(Socket clientSocket) {
        // Implement the seller process here
        System.out.println("Launching Seller Process for " + clientSocket.getInetAddress());
        // Add your seller-specific logic and process code
    }


    public static void main(String[] args) throws IOException {
        UserServer userServer = new UserServer();
        userServer.start();
    }
}
