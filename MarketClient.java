import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MarketClient extends JFrame {

    private PrintWriter writer;
    private BufferedReader reader;
    private JTextField nameTextField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JTextArea statusTextArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MarketClient marketClient = new MarketClient();
            marketClient.setVisible(true);
        });
    }

    public MarketClient() {
        try {
            String serverAddress = "localhost";
            int serverPort = 5432;

            Socket clientSocket = new Socket(serverAddress, serverPort);
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setTitle("Market Client");
            setSize(1000, 750);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(5, 2));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel nameLabel = new JLabel("Username:");
            nameTextField = new JTextField();
            JLabel passwordLabel = new JLabel("Password:");
            passwordField = new JPasswordField();
            JLabel roleLabel = new JLabel("Role:");
            roleComboBox = new JComboBox<>(new String[]{"buyer", "seller"});

            panel.add(nameLabel);
            panel.add(nameTextField);
            panel.add(passwordLabel);
            panel.add(passwordField);
            panel.add(roleLabel);
            panel.add(roleComboBox);

            JButton createUserButton = new JButton("Create User");
            createUserButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    createUser(nameTextField.getText(), new String(passwordField.getPassword()), roleComboBox.getSelectedItem().toString());
                }
            });

            JButton loginButton = new JButton("Login");
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    login(nameTextField.getText(), new String(passwordField.getPassword()), roleComboBox.getSelectedItem().toString());
                }
            });

            JButton deleteUserButton = new JButton("Delete User");
            deleteUserButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteUser(nameTextField.getText(), new String(passwordField.getPassword()), roleComboBox.getSelectedItem().toString());
                }
            });

            statusTextArea = new JTextArea();
            statusTextArea.setEditable(false);

            panel.add(createUserButton);
            panel.add(loginButton);
            panel.add(deleteUserButton);
            panel.add(statusTextArea);

            add(panel);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createUser(String email, String pwd, String userType) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "adduser");
        jsonMessage.put("email", email);
        jsonMessage.put("pwd", pwd);
        jsonMessage.put("type", userType);

        sendMessage(jsonMessage);
    }

    private void login(String email, String pwd, String userType) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "login");
        jsonMessage.put("email", email);
        jsonMessage.put("pwd", pwd);
        jsonMessage.put("type", userType);

        sendMessage(jsonMessage);
    }

    private void deleteUser(String email, String pwd, String userType) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "removeuser");
        jsonMessage.put("email", email);
        jsonMessage.put("pwd", pwd);
        jsonMessage.put("type", userType);

        sendMessage(jsonMessage);
    }

    private void sendMessage(JSONObject jsonMessage) {
        writer.println(jsonMessage.toJSONString());
        try {
            String serverResponse = reader.readLine();
            statusTextArea.setText("Server Response: " + serverResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
