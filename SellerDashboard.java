import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class SellerDashboard extends JFrame implements SellerInterface {
    private static String name = "";
    private static Map<String, StoreplaceManager> storeManagers = new HashMap<>();
    private static ReentrantLock editingLock = new ReentrantLock();

    public SellerDashboard(String name) {
        this.name = name;
        if (!Files.exists(Paths.get("store_data.csv"))) {
            try {
                Files.createFile(Paths.get("store_data.csv"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadStoresFromFile();

        setTitle("Seller Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Hello " + name);
        panel.add(nameLabel);

        JButton createButton = new JButton("Create Store");
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createStore();
            }
        });
        panel.add(createButton);

        JButton enterButton = new JButton("Enter Store");
        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enterStore();
            }
        });
        panel.add(enterButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveStoresToFile();
                System.exit(0);
            }
        });
        panel.add(exitButton);
    }

    public void createStore() {
        String storeName = JOptionPane.showInputDialog("Enter store name:");
        if (storeName != null) {
            synchronized (storeManagers) {
                if (!storeManagers.containsKey(storeName)) {
                    editingLock.lock();
                    try {
                        StoreplaceManager storeManager = new StoreplaceManager(name, storeName);
                        storeManagers.put(storeName, storeManager);

                        try (BufferedWriter writer = new BufferedWriter(new FileWriter("store_data.csv", true))) {
                            writer.write(name + "," + storeName);
                            writer.newLine();
                            System.out.println("Store '" + storeName + "' created successfully.");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } finally {
                        editingLock.unlock();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Store with the same name already exists.");
                }
            }
        }
    }

    public void enterStore() {
        String storeName = JOptionPane.showInputDialog("Enter store name:");
        if (storeName != null) {
            synchronized (storeManagers) {
                if (storeManagers.containsKey(storeName)) {
                    editingLock.lock();
                    try {
                        StoreplaceManager storeManager = storeManagers.get(storeName);
                        if (storeManager != null) {
                            storeManager.displayDashboard(storeName);
                        }
                    } finally {
                        editingLock.unlock();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Store not found. Please create the store first.");
                }
            }
        }
    }

    public void loadStoresFromFile() {
        synchronized (storeManagers) {

            try (BufferedReader reader = new BufferedReader(new FileReader("store_data.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] storeDetails = line.split(",");
                    if (storeDetails.length == 2) {
                        String sellerName = storeDetails[0].trim();
                        String storeName = storeDetails[1].trim();
                        storeManagers.put(storeName, new StoreplaceManager(sellerName, storeName));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveStoresToFile() {
        synchronized (storeManagers) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("store_data.csv"))) {
                for (Map.Entry<String, StoreplaceManager> entry : storeManagers.entrySet()) {
                    writer.write(name + "," + entry.getKey());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
