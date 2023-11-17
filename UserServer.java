import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class UserServer {
    private static final int PORT = 12345;
    private Map<String, String> userCredentials; // Map to store user credentials (email -> password)
    private Map<String, String> userRoles;       // Map to store user roles (email -> role)

    public UserServer() {
        userCredentials = new HashMap<>();
        userRoles = new HashMap<>();
        loadUserDataFromCSV("user_data.csv");
    }

    private void loadUserDataFromCSV(String csvFileName) {
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

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handle client connection in a separate thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String email = reader.readLine();
            String password = reader.readLine();

            // Check user credentials
            if (userCredentials.containsKey(email) && userCredentials.get(email).equals(password)) {
                // Send the user role back to the client
                String role = userRoles.get(email);
                writer.println(role);

                // Perform actions based on the user's role
                if ("seller".equals(role)) {
                    System.out.println("Seller functionality started for: " + email);
                } else {

                }
            } else {
                writer.println("INVALID");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UserServer userServer = new UserServer();
        userServer.start();
    }
}
