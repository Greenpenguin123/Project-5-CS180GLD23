import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

//public class BuyerRecordWnd extends JFrame {
public class ShoppingCartWnd extends JDialog {
    private DefaultTableModel tableModel;
    private JTable table;

    private String buyerName;

    public ShoppingCartWnd(JFrame parent, String buyer) {
        super(parent, "Shopping Cart for:" + buyer, true);
        this.buyerName = buyer;
        initialize();

    }

    private void initialize() {
        // Create a table model with columns: datetime, seller, store product, quantity,
        // price
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Seller");
        tableModel.addColumn("Store");
        tableModel.addColumn("Product");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Price");

        // Create the table
        table = new JTable(tableModel);
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Create a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(table);

        // Add a new row to the table
        // tableModel.addRow(new Object[] { datetime, seller, storeProduct, quantity,
        // price });

        // Create a panel for the table and input fields
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton BuyButton = new JButton("Buy!");
        BuyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Buy();
            }
        });

        JButton RemoveButton = new JButton("Remove");
        BuyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(BuyButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the main panel to the dialog
        getContentPane().add(mainPanel);

        ReadShoppingCart();

        // Set dialog properties
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Make the dialog visible
        setVisible(true);

    }

    private void ReadShoppingCart() {
        List<SaleRecordBuyer> result = CmdIO.ReadShoppingCart(buyerName);
        tableModel.setRowCount(0);
        for (SaleRecordBuyer sale : result) {
            tableModel.addRow(new Object[] { sale.sellerName, sale.storeName, sale.productName,
                    sale.quantity, sale.price });
        }
    }

    private void Buy()
    {
        int ret = CmdIO.ShoppingCartBuy(buyerName);
        JOptionPane.showMessageDialog(this, "Purchase " + (ret == 0 ? "succeed" : "failed:" + ret));
        ReadShoppingCart();
    }
}
