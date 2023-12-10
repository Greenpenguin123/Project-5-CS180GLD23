import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MarketClient extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginWnd marketSystemGUI = new LoginWnd();
        });
    }
}
