
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Interface for MarketData
public interface IMarketData{
    // seller
    List<String> ListStore(String seller);
    List<productBrowseResult> ListStoreProducts(String seller, String storeName);
    int AddStore(String seller, String storeName);
    int AddProduct(String seller, String storeName, String productName, String description, int quantity, double price);
    int RemoveProduct(String seller, String storeName, String productName);

    // buyer
    List<productBrowseResult> BrowseProduct(String seachKeyWords);
    List<SaleRecord> querybuyerPurchaseRecords(String buyer);
    List<ShoppingCartRecord> readShoppingCart(String buyer);
    int BuyProduct(String seller, String buyer, String storeName, String productName, int quantity, double price);

    // Todo shopping cart
    //List<productBrowseResult> ShoppingCart(String buyer);
    int AddShoppingCart(String buyer, String seller, String storeName, String productName, int quantity, double price);
    int ShoppingCartCommit(String buyer);
    int ShoppingCartRemove(String buyer, String storeName, String productName, int quantityRemoved);
}
