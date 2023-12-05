import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
/*
public class MarketplaceManager {
    private static final String MARKETPLACE_FILE = "MarketPlace.csv";
    private ArrayList<Product> productList;

    public MarketplaceManager(ArrayList<Product> productList) {
        this.productList = productList;
    }

    public void saveProductListToFile(ArrayList<Product> productList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Marketplace.csv"))) {
            writer.write("Name,Description,Store,Price,Quantity Available\n"); // Header
            for (Product product : productList) {
                writer.write(String.format("%s,%s,%s,%.2f,%d\n",
                        product.getName(),
                        product.getDescription(),
                        product.getStore(),
                        product.getPrice(),
                        product.getQuantityAvailable()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }
}*/