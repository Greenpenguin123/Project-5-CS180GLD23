import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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

public class BuyerWnd extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;

    private String buyerName;


    public BuyerWnd(String buyerName) {

        this.buyerName = buyerName;

        // products.add(new ProductForBuyer("Laptop", "Powerful laptop with high performance", 10, 1200.0));
        // products.add(new ProductForBuyer("Smartphone", "Latest smartphone with amazing features", 20, 800.0));
        // products.add(new ProductForBuyer("Headphones", "Noise-canceling headphones for immersive experience", 15, 150.0));

        tableModel = new DefaultTableModel(new Object[]{"Seller", "Store", "Product", "Description", "Quantity", "Price"}, 0) {
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
            public void actionPerformed(ActionEvent e) {
                AddToShoppingCart();
            }
        });



        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buyButton);
        buttonPanel.add(buyButton);
        buttonPanel.add(AddToShoppingCartButton);

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setTitle("Product Search App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        //for (Product product : products) {
        //    tableModel.addRow(new Object[]{product.getName(), product.getDescription(), product.getQuantity(), product.getPrice()});
        //}
    }

    private void searchProducts() {
        String keyword = searchField.getText().toLowerCase();


        List<BrowseProduct> searchResults = CmdIO.searchProduct(keyword);

        // Update the table with search results
        tableModel.setRowCount(0);
        for (BrowseProduct product : searchResults) {
            tableModel.addRow(new Object[]{
                    product.seller, product.store, product.product,product.description, product.quantity, product.price});
        }
    }

    private void buySelectedProduct() {
        int selectedRow = table.getSelectedRow();
        int quantityBought = 0;
        if (selectedRow != -1) {
            // Implement the logic for buying the product
            String inputText = JOptionPane.showInputDialog(null, "Please enter an integer:");

            try {
                // Convert the input text to an integer
                quantityBought = Integer.parseInt(inputText);
                // Process the integer value as needed
                JOptionPane.showMessageDialog(null, "You purchased: " + quantityBought + " items.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException | NullPointerException ex) {
                // Handle the case where the input is not a valid integer or the user cancels the input
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            String storeName = (String) table.getValueAt(selectedRow, 1);
            String productName = (String) table.getValueAt(selectedRow, 2);
            double price = (double) table.getValueAt(selectedRow, 5);
            int ret = CmdIO.purchaseProduct(buyerName, storeName, productName, quantityBought, price);
            if(ret == 0) {
                JOptionPane.showMessageDialog(null, "Purchase successful!", "Purchase Success", JOptionPane.INFORMATION_MESSAGE);
                searchProducts();
            } else {
                JOptionPane.showMessageDialog(null, "Purchase Failed!", "Purchase Failed", JOptionPane.WARNING_MESSAGE);

            }

        } else {
            // Show a message if no product is selected
            JOptionPane.showMessageDialog(this, "Please select a product to buy.", "No Product Selected", JOptionPane.WARNING_MESSAGE);
        }


    }
    private void AddToShoppingCart() {
        int selectedRow = table.getSelectedRow();
        int quantityBought = 0;
        if (selectedRow != -1) {
            // Implement the logic for buying the product
            String inputText = JOptionPane.showInputDialog(null, "Please enter an integer:");

            try {
                // Convert the input text to an integer
                quantityBought = Integer.parseInt(inputText);
                // Process the integer value as needed
                JOptionPane.showMessageDialog(null, "You added: " + quantityBought + " items to Shopping Cart", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException | NullPointerException ex) {
                // Handle the case where the input is not a valid integer or the user cancels the input
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            String sellerName = (String) table.getValueAt(selectedRow, 0);
            String storeName = (String) table.getValueAt(selectedRow, 1);
            String productName = (String) table.getValueAt(selectedRow, 2);
            double price = (double) table.getValueAt(selectedRow, 5);
            int ret = CmdIO.AddtoShoppingCart(buyerName, storeName, productName, sellerName, quantityBought, price);
            if (ret == 0) {
                JOptionPane.showMessageDialog(null, "Purchase added successfully!", "Added to Cart Successfully", JOptionPane.INFORMATION_MESSAGE);
                searchProducts();
            } else {
                JOptionPane.showMessageDialog(null, "Purchase added failed!", "Added to Cart Unsuccessful", JOptionPane.WARNING_MESSAGE);

            }


        }


    }
}
