import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.JOptionPane.*;

class BrowseProduct {
    public String seller;
    public String store;
    public String product;
    public String description;
    public int quantity;
    public double price;

    public BrowseProduct(String seller, String store, String product, String description, int quantity, double price) {
        this.seller = seller;
        this.store = store;
        this.product = product;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }
}

class SaleRecordBuyer {
    public String buyer;
    public String datetime;
    public String sellerName;
    public String storeName;
    public String productName;
    public int quantity;
    public double price;

    SaleRecordBuyer(String buyer, String datetime, String seller, String store, String product, int quantity,
                    double price) {
        this.buyer = buyer;
        this.datetime = datetime;
        sellerName = seller;
        storeName = store;
        productName = product;
        this.quantity = quantity;
        this.price = price;
    }
}

public class BuyerWnd extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;

    private String buyerName;

    public BuyerWnd(String buyerName) {

        this.buyerName = buyerName;

        // products.add(new ProductForBuyer("Laptop", "Powerful laptop with high
        // performance", 10, 1200.0));
        // products.add(new ProductForBuyer("Smartphone", "Latest smartphone with
        // amazing features", 20, 800.0));
        // products.add(new ProductForBuyer("Headphones", "Noise-canceling headphones
        // for immersive experience", 15, 150.0));

        tableModel = new DefaultTableModel(
                new Object[] { "Seller", "Store", "Product", "Description", "Quantity", "Price" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // updateTable();

        searchField = new JTextField(20);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchProducts();
            }
        });

        JButton buyButton = new JButton("Buy");
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buySelectedProduct();
            }
        });

        JButton AddToShoppingCartButton = new JButton("Add to Shopping Cart");
        AddToShoppingCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddToShoppingCart();
            }
        });

        JButton GoToShoppingCartButton = new JButton("Goto Shopping Cart..");
        GoToShoppingCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShowShoppingCart(BuyerWnd.this);
            }
        });

        JButton purchaseRecordButton = new JButton("Purchase history..");
        purchaseRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the SimpleFormWithTable dialog
                System.out.println("call ShowBuyerHistory");
                ShowBuyerHistory(BuyerWnd.this);

            }
        });

        JButton SignOut = new JButton("Sign Out");
        SignOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SignOut();
            }
        });

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buyButton);
        buttonPanel.add(purchaseRecordButton);
        buttonPanel.add(AddToShoppingCartButton);
        buttonPanel.add(GoToShoppingCartButton);
        buttonPanel.add(SignOut);

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setTitle("Buyer: " + buyerName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        // for (Product product : products) {
        // tableModel.addRow(new Object[]{product.getName(), product.getDescription(),
        // product.getQuantity(), product.getPrice()});
        // }
    }

    private void searchProducts() {
        String keyword = searchField.getText().toLowerCase();

        List<BrowseProduct> searchResults = CmdIO.searchProduct(keyword);

        // Update the table with search results
        tableModel.setRowCount(0);
        for (BrowseProduct product : searchResults) {
            tableModel.addRow(new Object[] {
                    product.seller, product.store, product.product, product.description, product.quantity,
                    product.price });
        }
    }

    private void buySelectedProduct() {
        int selectedRow = table.getSelectedRow();
        int quantityBought = 0;
        if (selectedRow != -1) {
            // Implement the logic for buying the product
            String inputText = showInputDialog(null, "Please enter number of purrchase items:");

            try {
                // Convert the input text to an integer
                quantityBought = Integer.parseInt(inputText);
                // Process the integer value as needed
                // JOptionPane.showMessageDialog(null, "You purchased: " + quantityBought + "
                // items.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException | NullPointerException ex) {
                // Handle the case where the input is not a valid integer or the user cancels
                // the input
                showMessageDialog(null, "Invalid input. Please enter a valid integer.", "Error",
                        ERROR_MESSAGE);
                return;
            }
            String seller = (String) table.getValueAt(selectedRow, 0);
            String storeName = (String) table.getValueAt(selectedRow, 1);
            String productName = (String) table.getValueAt(selectedRow, 2);
            double price = (double) table.getValueAt(selectedRow, 5);
            int ret = CmdIO.purchaseProduct(seller, buyerName, storeName, productName, quantityBought, price);
            if (ret == 0) {
                showMessageDialog(null, "Purchase successful!", "Purchase Success",
                        INFORMATION_MESSAGE);
                searchProducts();
            } else {
                showMessageDialog(null, "Purchase Failed!", "Purchase Failed", WARNING_MESSAGE);

            }

        } else {
            // Show a message if no product is selected
            showMessageDialog(this, "Please select a product to buy.", "No Product Selected",
                    WARNING_MESSAGE);
        }

    }

    private void ShowBuyerHistory(JFrame parent) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                BuyerRecordWnd buyhistoryWnd = new BuyerRecordWnd(parent, buyerName);

                 buyhistoryWnd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                 buyhistoryWnd.setLocationRelativeTo(null); // Center on screen
                 buyhistoryWnd.setModalExclusionType(Dialog.ModalExclusionType.
                 TOOLKIT_EXCLUDE.APPLICATION_EXCLUDE);
                 buyhistoryWnd.setVisible(true);

            }
        });
    }

    private void AddToShoppingCart() {
        int selectedRow = table.getSelectedRow();
        int quantityBought = 0;
        if (selectedRow != -1) {
            // Implement the logic for buying the product
            String inputText = showInputDialog(null, "Please enter number of itms:");

            try {
                // Convert the input text to an integer
                quantityBought = Integer.parseInt(inputText);
                // Process the integer value as needed
                showMessageDialog(null, "You added: " + quantityBought + " items to Shopping Cart",
                        "Success", INFORMATION_MESSAGE);
            } catch (NumberFormatException | NullPointerException ex) {
                // Handle the case where the input is not a valid integer or the user cancels
                // the input
                showMessageDialog(null, "Invalid input. Please enter a valid integer.", "Error",
                        ERROR_MESSAGE);
                return;
            }

            if (quantityBought <= 0) {
                showMessageDialog(null, "Invalid input. Please enter a valid integer.", "Error",
                        ERROR_MESSAGE);
                return;
            }
            String seller = (String) table.getValueAt(selectedRow, 0);
            String storeName = (String) table.getValueAt(selectedRow, 1);
            String productName = (String) table.getValueAt(selectedRow, 2);
            double price = (double) table.getValueAt(selectedRow, 5);

            int ret = CmdIO.AddtoShoppingCart(buyerName, storeName, seller, productName, quantityBought, price);
            if (ret == 0) {
                showMessageDialog(null, "Purchase added successfully!", "Added to Cart Successfully",
                        INFORMATION_MESSAGE);
                searchProducts();
            } else {
                showMessageDialog(null, "Purchase added failed!", "Added to Cart Unsuccessful",
                        WARNING_MESSAGE);

            }

        }
    }

    private void ShowShoppingCart(JFrame parent) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ShoppingCartWnd buyhistoryWnd = new ShoppingCartWnd(parent, buyerName);
                /*
                 * buyhistoryWnd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                 * buyhistoryWnd.setLocationRelativeTo(null); // Center on screen
                 * buyhistoryWnd.setModalExclusionType(Dialog.ModalExclusionType.
                 * APPLICATION_EXCLUDE);
                 * buyhistoryWnd.setVisible(true);
                 */
            }
        });
    }
    private void SignOut() {
        int response = showConfirmDialog(this, "Are you sure you want to sign out?", "Sign Out",
            YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            dispose();
        }
    }
}
