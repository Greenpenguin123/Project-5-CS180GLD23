import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Product {
    String name;
    String description;
    int quantity;
    double price;

    public Product(String name, String description, int quantity, double price) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }
}

class Store {
    String name;
    List<Product> products;

    public Store(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }
}

public class MarketClient extends JFrame {
   
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginWnd marketSystemGUI = new LoginWnd();
            // LoginWnd.setVisible(true);
        });
    }
}
