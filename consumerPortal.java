import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class consumerPortal extends JFrame {
    public static void openConsumerPortal(String userName) {
        Consumer consumer = new Consumer(userName);
        Path pathToWatch = Paths.get("/Users/sameermurthy/Library/Mobile Documents/com~apple~CloudDocs/Documents/Purdue /1st year/Project-5-CS180GLD23");

        FileWatcher watcher = new FileWatcher(pathToWatch, consumer);
        Thread watcherThread = new Thread(watcher);
        watcherThread.start();

        JFrame consumerFrame = new JFrame(userName + "'s Consumer Portal");
        int rows = 4; // Adjust the number of rows as needed
        int cols = 3; // Adjust the number of columns as needed
        consumerFrame.setLayout(new GridLayout(rows, cols));
        consumerFrame.setSize(600, 400);
        consumerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Button to view marketplace
        JButton viewMarketButton = new JButton("View Marketplace");
        viewMarketButton.addActionListener(e -> {
            Consumer.viewMarketPlace();
        });

        // Button to add a product
        JButton addProductButton = new JButton("Add Product to Cart");
        addProductButton.addActionListener(e -> {
            ArrayList<Product> products = consumer.returnProductList();
            String[] productNames = new String[products.size()];

            for (int i = 0; i < products.size(); i++) {
                productNames[i] = products.get(i).getName();
            }

            String selectedProductName = (String) JOptionPane.showInputDialog(null,
                    "Select a product to add to your cart:",
                    "Add Product", JOptionPane.QUESTION_MESSAGE, null,
                    productNames, productNames[0]);

            if (selectedProductName != null && !selectedProductName.isEmpty()) {
                for (Product product : products) {
                    if (product.getName().equals(selectedProductName)) {
                        consumer.addProduct(product);
                        JOptionPane.showMessageDialog(null, "Product added to cart successfully.", "Product Added", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                }
            }
        });

        JButton removeProductButton = new JButton("Remove Product from Cart");
        removeProductButton.addActionListener(e -> {
            ArrayList<String> cartItems = consumer.getCartList();
            String[] productNames = new String[cartItems.size()];

            for (int i = 0; i < cartItems.size(); i++) {
                String[] splitLine = cartItems.get(i).split(",");
                productNames[i] = splitLine[0];
            }

            String selectedProductName = (String) JOptionPane.showInputDialog(null,
                    "Select a product to remove from your cart:",
                    "Remove Product", JOptionPane.QUESTION_MESSAGE, null,
                    productNames, productNames.length > 0 ? productNames[0] : null);

            if (selectedProductName != null && !selectedProductName.isEmpty()) {
                consumer.removeProduct(selectedProductName);
                JOptionPane.showMessageDialog(null, "Product removed from cart successfully.", "Product Removed", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Button to view cart
        JButton viewCartButton = new JButton("View Cart");
        viewCartButton.addActionListener(e -> {
            consumer.printCart();
        });

        // Button to purchase products
        JButton purchaseButton = new JButton("Purchase Products");
        purchaseButton.addActionListener(e -> {
            consumer.purchaseProduct();
            // Display purchase confirmation
        });

        // Button to view purchase history
        JButton purchaseHistoryButton = new JButton("View Purchase History");
        purchaseHistoryButton.addActionListener(e -> {
            consumer.printPurchaseHistory();
        });

        //button to signout
        JButton signOutButton = new JButton("SignOut");
        signOutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Signed-out successfully.", "signout", JOptionPane.INFORMATION_MESSAGE);
            consumerFrame.dispose();
        });

        //button for search
        JButton searchProductsButton = new JButton("Search Products");
        searchProductsButton.addActionListener(e -> {
            consumer.searchProducts(consumer.returnProductList());
        });

        //button for sort
        JButton sortMarketplaceButton = new JButton("Sort MarketPlace");
        sortMarketplaceButton.addActionListener(e -> {
            consumer.sortMarketPlaceGUI(consumer.returnProductList());
        });

        //button for exporting purchase history
        JButton exportPurchaseHistoryButton = new JButton("Export Purchase History");
        exportPurchaseHistoryButton.addActionListener(e -> {
            consumer.exportPurchaseHistoryGUI();
        });

        //button to view dashboard
        JButton viewDashBoardButton = new JButton("View Seller Dashboard");
        viewDashBoardButton.addActionListener(e -> {
            consumer.viewDashboardGUI(consumer.returnProductList());
        });

        //button to sort dashboard
        JButton sortDashBoardButton = new JButton("Sort Seller Dashboard");
        sortDashBoardButton.addActionListener(e -> {
            consumer.sortDashboardGUI();
        });

        //button to show description
        JButton showDescriptionButton = new JButton("Show Description");
        showDescriptionButton.addActionListener(e -> {
            Consumer.showDescriptionGUI(consumer.returnProductList());
        });


        // Add buttons to the frame
        consumerFrame.add(viewMarketButton);
        consumerFrame.add(addProductButton);
        consumerFrame.add(removeProductButton);
        consumerFrame.add(viewCartButton);
        consumerFrame.add(purchaseButton);
        consumerFrame.add(purchaseHistoryButton);
        consumerFrame.add(searchProductsButton);
        consumerFrame.add(sortMarketplaceButton);
        consumerFrame.add(exportPurchaseHistoryButton);
        consumerFrame.add(viewDashBoardButton);
        consumerFrame.add(sortDashBoardButton);
        consumerFrame.add(showDescriptionButton);
        consumerFrame.add(signOutButton);

        consumerFrame.setVisible(true);
    }

    public static void main(String[] args) {
        openConsumerPortal("sampleUser123");
    }


}
