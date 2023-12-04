import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWnd extends JFrame {

    private JTextField serverAddressField, portField, usernameField;
    private JPasswordField passwordField;
    private JRadioButton sellerRadioButton, buyerRadioButton;

    public LoginWnd() {
        super("Login Window");

        // Create components
        JLabel serverAddressLabel = new JLabel("Server Address:");
        JLabel portLabel = new JLabel("Port:");
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel userTypeLabel = new JLabel("User Type:");

        serverAddressField = new JTextField();
        portField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        sellerRadioButton = new JRadioButton("Seller");
        buyerRadioButton = new JRadioButton("Buyer");
        ButtonGroup userTypeGroup = new ButtonGroup();
        userTypeGroup.add(sellerRadioButton);
        userTypeGroup.add(buyerRadioButton);

        JButton loginButton = new JButton("Login");
        JButton createAccountButton = new JButton("Create New Account");

        // Set layout
        setLayout(new GridLayout(8, 2, 5, 5));

        // Add components to the frame
        add(serverAddressLabel);

        serverAddressField.setText("localhost");
        add(serverAddressField);
        add(portLabel);
        portField.setText("5432");
        add(portField);
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(userTypeLabel);
        add(sellerRadioButton);
        add(new JLabel()); // Empty label for spacing
        add(buyerRadioButton);
        add(new JLabel()); // Empty label for spacing
        add(loginButton);
        add(new JLabel()); // Empty label for spacing
        add(createAccountButton);

        sellerRadioButton.setSelected(true);

        // Set action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle login logic here
                String serverAddress = serverAddressField.getText();
                int port = Integer.parseInt(portField.getText());
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String userType = sellerRadioButton.isSelected() ? "seller" : "buyer";

                // Perform login validation here
                // Example: Check credentials against a database
                int ret = CmdIO.Login(serverAddress, port, username, password, userType);

                if (ret == 0) {
                    // For simplicity, just show a message for now
                    JOptionPane.showMessageDialog(LoginWnd.this, "Login Successful as " + userType + "!");
                } else {
                    JOptionPane.showMessageDialog(LoginWnd.this, "Login failed as " + userType + "!");
                    return;
                }

                // Launch another frame window (replace MyOtherFrame with your actual frame
                // class)
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                                        // Close the login window
                        dispose();
                        SellerWnd sellerWnd = new SellerWnd(username);
                        sellerWnd.setVisible(true);
                    }
                });
            }
        });

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle new account creation logic here
                // Example: Open a new window for account creation
                JOptionPane.showMessageDialog(LoginWnd.this, "Opening New Account Window...");
            }
        });

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

}
