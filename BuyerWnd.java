import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    TableRowSorter<TableModel> sorter;

    public BuyerWnd(String buyerName) {

        this.buyerName = buyerName;

        tableModel = new DefaultTableModel(
                new Object[] { "Seller", "Store", "Product", "Description", "Quantity", "Price" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

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

        JButton GoToShoppingCartButton = new JButton("Go to Shopping Cart..");
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
        JButton signOutButton = new JButton("Sign Out");
        signOutButton.addActionListener(e -> dispose());

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buyButton);
        buttonPanel.add(purchaseRecordButton);
        buttonPanel.add(AddToShoppingCartButton);
        buttonPanel.add(GoToShoppingCartButton);
        buttonPanel.add(signOutButton);

        JRadioButton sortQuantity = new JRadioButton("Quantity");
        JRadioButton sortPrice = new JRadioButton("Price");
        JLabel selectedLabel = new JLabel("Sort on: ");
        JPanel radiobuttonPanel = new JPanel();
        radiobuttonPanel.add(selectedLabel);
        radiobuttonPanel.add(sortQuantity);
        radiobuttonPanel.add(sortPrice);

        ButtonGroup sortGroup = new ButtonGroup();
        sortGroup.add(sortQuantity);
        sortGroup.add(sortPrice);

        ActionListener radioListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JRadioButton selectedRadioButton = (JRadioButton) e.getSource();
                TableSort(selectedRadioButton.getText());
            }
        };

        sortQuantity.addActionListener(radioListener);
        sortPrice.addActionListener(radioListener);

        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.add(radiobuttonPanel, BorderLayout.NORTH);
        buttonPanel2.add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel2.setLayout(new BoxLayout(buttonPanel2, BoxLayout.Y_AXIS));

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel2, BorderLayout.SOUTH);

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
            String inputText = JOptionPane.showInputDialog(null, "Please enter number of items to be purchased:");

            try {
                // Convert the input text to an integer
                quantityBought = Integer.parseInt(inputText);
                // Process the integer value as needed
                // JOptionPane.showMessageDialog(null, "You purchased: " + quantityBought + "
                // items.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException | NullPointerException ex) {
                // Handle the case where the input is not a valid integer or the user cancels
                // the input
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            String seller = (String) table.getValueAt(selectedRow, 0);
            String storeName = (String) table.getValueAt(selectedRow, 1);
            String productName = (String) table.getValueAt(selectedRow, 2);
            double price = (double) table.getValueAt(selectedRow, 5);
            int ret = CmdIO.purchaseProduct(seller, buyerName, storeName, productName, quantityBought, price);
            if (ret == 0) {
                JOptionPane.showMessageDialog(null, "Purchase successful!", "Purchase Success",
                        JOptionPane.INFORMATION_MESSAGE);
                searchProducts();
            } else {
                JOptionPane.showMessageDialog(null, "Purchase Failed!", "Purchase Failed", JOptionPane.WARNING_MESSAGE);

            }

        } else {
            // Show a message if no product is selected
            JOptionPane.showMessageDialog(this, "Please select a product to buy.", "No Product Selected",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    private void ShowBuyerHistory(JFrame parent) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                BuyerRecordWnd buyhistoryWnd = new BuyerRecordWnd(parent, buyerName);
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

    private void AddToShoppingCart() {
        int selectedRow = table.getSelectedRow();
        int quantityBought = 0;
        if (selectedRow != -1) {
            // Implement the logic for buying the product
            String inputText = JOptionPane.showInputDialog(null, "Please enter number of itms:");

            try {
                // Convert the input text to an integer
                quantityBought = Integer.parseInt(inputText);
                // Process the integer value as needed
                JOptionPane.showMessageDialog(null, "You added: " + quantityBought + " items to Shopping Cart",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException | NullPointerException ex) {
                // Handle the case where the input is not a valid integer or the user cancels
                // the input
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (quantityBought <= 0) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            String seller = (String) table.getValueAt(selectedRow, 0);
            String storeName = (String) table.getValueAt(selectedRow, 1);
            String productName = (String) table.getValueAt(selectedRow, 2);
            double price = (double) table.getValueAt(selectedRow, 5);

            int ret = CmdIO.AddtoShoppingCart(buyerName, storeName, seller, productName, quantityBought, price);
            if (ret == 0) {
                JOptionPane.showMessageDialog(null, "Purchase added successfully!", "Added to Cart Successfully",
                        JOptionPane.INFORMATION_MESSAGE);
                searchProducts();
            } else {
                JOptionPane.showMessageDialog(null, "Purchase added failed!", "Added to Cart Unsuccessful",
                        JOptionPane.WARNING_MESSAGE);

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

    private void TableSort(String sort) {
        int columnIndex = 4;
        if (sort.equals("Quantity")) {
            columnIndex = 4;
        } else {
            columnIndex = 5; // Index of the "Age" column
        }
        sorter.setComparator(columnIndex, Comparator.naturalOrder());
        sorter.toggleSortOrder(columnIndex);

    }

}

//public class BuyerRecordWnd extends JFrame {

