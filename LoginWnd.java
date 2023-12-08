import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWnd extends JFrame {

    private JTextField serverAddressField, portField, usernameField;
    private JPasswordField passwordField;
    private JRadioButton sellerRadioButton, buyerRadioButton, buyerLocalButton;
    private JTextArea statusTextArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MarketClient marketClient = new MarketClient();
            marketClient.setVisible(true);
        });
    }

    public LoginWnd() {
        super("Market Client");

        // Create components
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel userTypeLabel = new JLabel("User Type:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        sellerRadioButton = new JRadioButton("Seller");
        buyerRadioButton = new JRadioButton("Buyer");
        buyerLocalButton = new JRadioButton("Buyer Local");
        ButtonGroup userTypeGroup = new ButtonGroup();
        userTypeGroup.add(sellerRadioButton);
        userTypeGroup.add(buyerRadioButton);
        userTypeGroup.add(buyerLocalButton);

        JButton loginButton = new JButton("Login");
        JButton createAccountButton = new JButton("Create New Account");
        JButton deleteUserButton = new JButton("Delete User");

        statusTextArea = new JTextArea();
        statusTextArea.setEditable(false);

        // Set layout
        setLayout(new GridLayout(10, 2, 5, 5));

        // Add components to the frame
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(userTypeLabel);
        add(sellerRadioButton);
        add(new JLabel()); // Empty label for spacing
        add(buyerRadioButton);
        add(new JLabel());
        add(buyerLocalButton);

        add(createAccountButton);
        add(loginButton);
        add(deleteUserButton);
        add(statusTextArea);

        sellerRadioButton.setSelected(true);
        String serverAddress = "localhost";
        int port = 5432;
        if(CmdIO.connect(serverAddress, port) != 0)
        {
            return;
        }

        // Set action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle login logic here
                ///String serverAddress = "localhost";
                //int port = 5432;
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String userType = sellerRadioButton.isSelected() ? "seller" : "buyer";

                int ret = CmdIO.Login(username, password, userType);

                if (ret == 0) {
                    JOptionPane.showMessageDialog(LoginWnd.this, "Login Successful as " + userType + "!");
                } else {
                    JOptionPane.showMessageDialog(LoginWnd.this, "Login failed as " + userType + "!");
                    return;
                }

                if (userType.equals("seller")) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // Close the login window
                            dispose();
                            SellerWnd sellerWnd = new SellerWnd(username);
                            sellerWnd.setVisible(true);
                        }
                    });
                } else if (buyerRadioButton.isSelected()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // Close the login window
                            dispose();
                            BuyerWnd buyerWnd = new BuyerWnd(username);
                            buyerWnd.setVisible(true);
                        }
                    });

                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // Close the login window
                            dispose();
                            // consumerPortal.openConsumerPortal(username);
                        }
                    });
                }
            }
        });

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String userType = sellerRadioButton.isSelected() ? "seller" : "buyer";

                int ret = CmdIO.AddLogin(username, password, userType);
                if (ret == 0) {
                    JOptionPane.showMessageDialog(LoginWnd.this, "Account creation successful as " + userType + "!");
                } else {
                    JOptionPane.showMessageDialog(LoginWnd.this, "Account creation unsuccessful as " + userType + "!");
                }
                return;
            }
        });



        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }
}
