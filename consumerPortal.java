import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class consumerPortal extends JFrame {
    private void openConsumerPortal(String userName) {
        Consumer consumer = new Consumer(userName);

        JFrame consumerFrame = new JFrame(userName + "'s Consumer Portal");
        consumerFrame.setLayout(new FlowLayout());
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
            // Display purchase history in a new window or a dialog
        });

        //button to signout
        JButton signOutButton = new JButton("SignOut");
        signOutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Signed-out successfully.", "signout", JOptionPane.INFORMATION_MESSAGE);
        });


        // Add buttons to the frame
        consumerFrame.add(viewMarketButton);
        consumerFrame.add(addProductButton);
        consumerFrame.add(removeProductButton);
        consumerFrame.add(viewCartButton);
        consumerFrame.add(purchaseButton);
        consumerFrame.add(purchaseHistoryButton);
        consumerFrame.add(signOutButton);

        consumerFrame.setVisible(true);
    }

}
