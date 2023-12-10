import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BuyerRecordWnd extends JDialog {
    private DefaultTableModel tableModel;
    private JTable table;

    private String buyerName;

    public BuyerRecordWnd(JFrame parent, String buyer)
    {
        super(parent, "Pruchase history for:" + buyer, true);
        this.buyerName = buyer;
        initialize();

    }

    private void initialize() {
        // Create a table model with columns: datetime, seller, store product, quantity,
        // price
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Datetime");
        tableModel.addColumn("Seller");
        tableModel.addColumn("Store");
        tableModel.addColumn("Product");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Price");

        // Create the table
        table = new JTable(tableModel);

        // Create a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(table);

        // Add a new row to the table
        // tableModel.addRow(new Object[] { datetime, seller, storeProduct, quantity,
        // price });

        // Create a panel for the table and input fields
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add the main panel to the dialog
        getContentPane().add(mainPanel);

        ReadPurchaseHistory();

        // Set dialog properties
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Make the dialog visible
        setVisible(true);


    }

    private void ReadPurchaseHistory() {
        List<SaleRecordBuyer> result = CmdIO.searchBuyerPurchaseRecords(buyerName);

        for (SaleRecordBuyer sale : result) {
            tableModel.addRow(new Object[] { sale.datetime, sale.sellerName, sale.storeName, sale.productName,
                    sale.quantity, sale.price });
        }
    }
}