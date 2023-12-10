import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;

import java.util.Comparator;
import java.util.List;

public class StoreSaleRecordsWnd extends JDialog {
    private DefaultTableModel tableModel;
    private JTable table;
    private String sellerName;
    private String storeName;

    TableRowSorter<TableModel> sorter;

    public StoreSaleRecordsWnd(JFrame parent, String seller, String store) {
        super(parent, "Pruchase history for:" + store, true);
        this.sellerName = seller;
        this.storeName = store;
        initialize();

    }

    private void initialize() {
        // Create a table model with columns: datetime, seller, store product, quantity,
        // price
        tableModel = new DefaultTableModel(
                new Object[] { "Datetime", "buyer", "Product", "Quantity", "Price" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        // Create the table
        table = new JTable(tableModel);
        ListSelectionModel selectionModel = table.getSelectionModel();
        JTableHeader tableHeader = table.getTableHeader();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        tableHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = tableHeader.columnAtPoint(e.getPoint());
                if(column ==3 || column == 4)
                {
                    sorter.setComparator(column, Comparator.naturalOrder());
                }
            }
        });

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
        List<SaleRecordBuyer> result = CmdIO.searchStorePurchaseRecords(sellerName, storeName);

        for (SaleRecordBuyer sale : result) {
            tableModel.addRow(new Object[] { sale.datetime, sale.buyer, sale.productName, sale.quantity, sale.price });
        }
    }
}
