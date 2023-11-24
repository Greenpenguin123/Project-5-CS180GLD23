import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UserAccountManager extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(UserAccountManager.class.getName());

    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JTextField serverAddressField;
    private JTextField serverPortField;
    private JLabel statusLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                setupLogger();
                new UserAccountManager().setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public UserAccountManager() {
        setTitle("P4 Marketplace User Account Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 280);
        setLocationRelativeTo(null);

        setupUI();
    }

    private void setupUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JLabel roleLabel = new JLabel("Role:");
        String[] roles = {"buyer", "seller"};
        roleComboBox = new JComboBox<>(roles);
        JLabel serverAddressLabel = new JLabel("Server Address:");
        serverAddressField = new JTextField();
        JLabel serverPortLabel = new JLabel("Server Port:");
        serverPortField = new JTextField();

        JButton createButton = new JButton("Create Account");
        JButton signInButton = new JButton("Sign In");

        statusLabel = new JLabel("");

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createUser();
            }
        });

        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signInUser();
            }
        });

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(roleLabel);
        panel.add(roleComboBox);
        panel.add(serverAddressLabel);
        panel.add(serverAddressField);
        panel.add(serverPortLabel);
        panel.add(serverPortField);
        panel.add(createButton);
        panel.add(signInButton);

        add(panel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void createUser() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        String serverAddress = serverAddressField.getText();
        int serverPort = Integer.parseInt(serverPortField.getText());

        // Validation checks for fields can be added here

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send create request to server
            writer.println("CREATE");
            writer.println(email);
            writer.println(password);
            writer.println(role);

            // Receive response from the server
            String response = reader.readLine();

            if ("SUCCESS".equals(response)) {
                statusLabel.setText("User created successfully!");
            } else {
                statusLabel.setText("Failed to create user. Please try again.");
            }

        } catch (IOException e) {
            statusLabel.setText("Failed to connect to the server. Please check server address and port.");
            e.printStackTrace();
        }
    }

    private void signInUser() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String role = new String(String.valueOf(roleComboBox.getSelectedItem()));
        String serverAddress = serverAddressField.getText();
        int serverPort = Integer.parseInt(serverPortField.getText());

        // Validation checks for fields can be added here

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send login request to server
            writer.println("LOGIN");
            writer.println(email);
            writer.println(password);
            writer.println(role);

            // Receive response from the server
            String response = reader.readLine();

            if ("SUCCESS".equals(response)) {
                statusLabel.setText("User signed in successfully!");
                JOptionPane.showMessageDialog(null, "Opening " + role + " portal");


            } else {
                statusLabel.setText("Login failed. Invalid username or password.");
            }

        } catch (IOException e) {
            statusLabel.setText("Failed to connect to the server. Please check server address and port.");
            e.printStackTrace();
        }
    }

    private static void setupLogger() throws IOException {
        FileHandler fileHandler = new FileHandler("user_operations.log", true);
        fileHandler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(fileHandler);
    }
}