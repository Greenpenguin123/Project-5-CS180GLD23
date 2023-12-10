import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

// for server
class ProductSeller {
    String name;
    String description;
    int quantity;
    double price;

    public ProductSeller(String name, String description, int quantity, double price) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }
}

class StoreSeller {
    String name;
    List<ProductSeller> products;

    public StoreSeller(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }
}

public class SellerWnd extends JFrame {
    private DefaultListModel<String> storeListModel;
    private DefaultTableModel productTableModel;
    private JTextField productNameField, productDescField, productQuantityField, productPriceField;
    private JList<String> storeList;
    private JTable productListTable;
    // private JComboBox<String> storeComboBox;

    private Map<String, StoreSeller> stores;

    private String sellerName;

    public SellerWnd(String sellerName) {

        this.sellerName = sellerName;
        setTitle("Seller: " + sellerName);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        stores = new HashMap<>();

        // Create components
        storeListModel = new DefaultListModel<>();
        storeList = new JList<>(storeListModel);
        ListSelectionModel selectionModel = storeList.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // storeComboBox = new JComboBox<>();

        productTableModel = new DefaultTableModel(new Object[] { "Name", "Description", "Quantity", "Price" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells non-editable
                return false;
            }
        };
        productListTable = new JTable(productTableModel);
        selectionModel = productListTable.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = productListTable.getSelectedRow();
                    System.out.println("Selected Row: " + selectedRow);
                    if (selectedRow != -1) {
                        productNameField.setText((String) productListTable.getValueAt(selectedRow, 0));
                        productDescField.setText((String) productListTable.getValueAt(selectedRow, 1));
                        productQuantityField.setText(("" + (int) productListTable.getValueAt(selectedRow, 2)));
                        productPriceField.setText("" + (double) productListTable.getValueAt(selectedRow, 3));
                    }

                }
            }
        });

        productNameField = new JTextField(20);
        productDescField = new JTextField(20);
        productQuantityField = new JTextField(5);
        productPriceField = new JTextField(5);

        JButton addProductButton = new JButton("Add Product");
        JButton refreshProductButton = new JButton("Refresh ProductList");
        JButton deleteProductButton = new JButton("Delete Product");
        JButton storeSaleRecordsButton = new JButton("Store sale records..");
        JButton shoppingCartButton = new JButton("priducts in shopping Cart..");

        JButton createStoreButton = new JButton("Create Store..");

        // Layout
        setLayout(new BorderLayout());

        JPanel storePanel = new JPanel(new BorderLayout());
        storePanel.add(createStoreButton, BorderLayout.NORTH);
        storePanel.add(new JScrollPane(storeList), BorderLayout.CENTER);
        // storePanel.add(storeComboBox, BorderLayout.SOUTH);

        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.add(new JScrollPane(productListTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(7, 2));
        formPanel.add(new JLabel("Name:"));
        formPanel.add(productNameField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(productDescField);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(productQuantityField);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(productPriceField);
        formPanel.add(addProductButton);
        formPanel.add(deleteProductButton);
        formPanel.add(refreshProductButton);
        formPanel.add(storeSaleRecordsButton);
        formPanel.add(shoppingCartButton);

        productPanel.add(formPanel, BorderLayout.SOUTH);

        add(storePanel, BorderLayout.WEST);
        add(productPanel, BorderLayout.CENTER);
        // add(deleteProductButton, BorderLayout.SOUTH);

        createStoreButton.addActionListener(e -> createStore());

        // Listeners
        storeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedStoreName = storeList.getSelectedValue();
                if (selectedStoreName != null) {
                    updateProductTable(selectedStoreName);
                }
            }
        });

        addProductButton.addActionListener(e -> addProduct());

        refreshProductButton.addActionListener(e -> RefreshProduct());

        deleteProductButton.addActionListener(e -> deleteProduct());

        storeSaleRecordsButton.addActionListener(e -> storesalesRecord());

        shoppingCartButton.addActionListener(e -> storeShoppingCart());

        /*
         * storeComboBox.addActionListener(e -> {
         * String selectedStoreName = (String) storeComboBox.getSelectedItem();
         * if (selectedStoreName != null) {
         * updateProductForm(selectedStoreName);
         * }
         * });
         */

        refreshStoreList();
    }

    private void updateProductTable(String storeName) {
        productTableModel.setRowCount(0);
        List<ProductSeller> products = CmdIO.queryStoreProduct(sellerName, storeName);
        for (ProductSeller product : products) {
            productTableModel
                    .addRow(new Object[] { product.name, product.description, product.quantity, product.price });
        }
    }

    private void refreshStoreList() {
        List<String> stores = CmdIO.queryStores(sellerName);
        storeListModel.clear();
        for (String storeName : stores) {
            storeListModel.addElement(storeName);
        }
    }

    private void createStore() {
        // Open a popup window to enter the store name
        String storeName = JOptionPane.showInputDialog(this, "Enter Store Name:", "Create Store",
                JOptionPane.PLAIN_MESSAGE);

        if (storeName != null && !storeName.trim().isEmpty()) {

            int ret = CmdIO.createStore(sellerName, storeName);
            JOptionPane.showMessageDialog(this, "Create store " + (ret == 0 ? "succeed" : "failed:" + ret));

            if (ret != 0) {
                return;
            }
            // Add the new store to the map
            // Update the store list
            storeListModel.addElement(storeName);

            storeList.setSelectedValue(storeName, true);

            // Update the product table
            updateProductTable(storeName);
        }
    }

    private void updateProductForm(String storeName) {
        productNameField.setText("");
        productDescField.setText("");
        productQuantityField.setText("");
        productPriceField.setText("");
        /*
         * StoreSeller store = stores.get(storeName);
         * if (store != null) {
         * for (ProductSeller product : store) {
         * productNameField.setText(product.getName());
         * productDescField.setText(product.getDescription());
         * productQuantityField.setText(String.valueOf(product.getQuantityAvailable()));
         * productPriceField.setText(String.valueOf(product.getPrice()));
         * }
         * }
         */
    }

    private void addProduct() {
        String storeName = storeList.getSelectedValue();
        if (storeName != null) {
            try {
                String name = productNameField.getText();
                String description = productDescField.getText();
                int quantity = Integer.parseInt(productQuantityField.getText());
                double price = Double.parseDouble(productPriceField.getText());

                int ret = CmdIO.addProduct(sellerName, storeName, name, description, quantity, price);
                JOptionPane.showMessageDialog(this, "Add product" + (ret == 0 ? "succeed" : "failed:" + ret));

                if (ret != 0) {
                    return;
                }

                updateProductTable(storeName);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity or price.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void RefreshProduct() {
        String storeName = storeList.getSelectedValue();
        if (storeName != null) {
            updateProductTable(storeName);
        }
    }

    private void deleteProduct() {
        String storeName = storeList.getSelectedValue();
        if (storeName != null) {
            try {
                String name = productNameField.getText();
                String description = productDescField.getText();

                int ret = CmdIO.removeProduct(sellerName, storeName, name);
                JOptionPane.showMessageDialog(this, "Remove product" + (ret == 0 ? "succeed" : "failed:" + ret));

                if (ret != 0) {
                    return;
                }

                updateProductTable(storeName);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity or price.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void storesalesRecord() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String storeName = storeList.getSelectedValue();
                if (storeName != null) {

                    StoreSaleRecordsWnd saleRecordsWnd = new StoreSaleRecordsWnd(SellerWnd.this, sellerName, storeName);
                }
            }
        });
    }

    private void storeShoppingCart()
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String storeName = storeList.getSelectedValue();
                if (storeName != null) {

                    StoreShoppingCartWnd saleRecordsWnd = new StoreShoppingCartWnd(SellerWnd.this, sellerName, storeName);
                }
            }
        });
    }
}
