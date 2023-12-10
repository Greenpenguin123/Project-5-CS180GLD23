import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StoreShoppingCartWnd extends JDialog {
    private DefaultTableModel tableModel;
    private JTable table;

    private String seller;
    private String store;

    TableRowSorter<TableModel> sorter;

    public StoreShoppingCartWnd(JFrame parent, String seller, String store) {
        super(parent, "Product in Shopping Cart for:" + seller, true);
        this.seller = seller;
        this.store = store;
        initialize();

    }

    private void initialize() {
        // Create a table model with columns: datetime, seller, store product, quantity,
        // price
        tableModel = new DefaultTableModel(
                new Object[] { "buyer", "Product", "Quantity", "Price" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = tableHeader.columnAtPoint(e.getPoint());
                if(column ==2 || column == 3)
                {
                    sorter.setComparator(column, Comparator.naturalOrder());
                }
            }
        });


        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Create a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(table);

        // Add the main panel to the dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        getContentPane().add(mainPanel);

        ReadShoppingCartinStore();

        // Set dialog properties
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Make the dialog visible
        setVisible(true);

    }

    private void ReadShoppingCartinStore() {
        List<SaleRecordBuyer> result = CmdIO.ReadShoppingCartInStore(seller, store);
        tableModel.setRowCount(0);
        for (SaleRecordBuyer sale : result) {
            tableModel.addRow(new Object[] { sale.buyer, sale.productName,
                    sale.quantity, sale.price });
        }
    }

}
