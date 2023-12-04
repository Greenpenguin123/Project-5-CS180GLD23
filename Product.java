import org.json.simple.JSONObject;

public class Product {
    private String name;
    private String description;
    private String store;
    private int itemsSold;
    private int quantityAvailable;
    private double price;

    public Product(String name, String description, String store, double price, int quantityAvailable) {
        this.name = name;
        this.description = description;
        this.store = store;
        this.itemsSold = 0;
        this.price = price;
        this.quantityAvailable = quantityAvailable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getItemsSold() {
        return itemsSold;
    }

    public void setItemsSold(int itemsSold) {
        this.itemsSold = itemsSold;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }
    public void incrementItemsSoldBy(int quantity) {
        this.itemsSold += quantity;
    }

    @Override
    public String toString() {
        return name + " ----- " + store + " ----- " + price;
    }

    public String printForSort() {
        return name + " ----- " + store + " ----- " + price + "-----" + quantityAvailable;
    }

    public JSONObject toJSON() {
        JSONObject jsonProduct = new JSONObject();
        jsonProduct.put("name", name);
        jsonProduct.put("description", description);
        jsonProduct.put("store",store);
        jsonProduct.put("price", price);
        jsonProduct.put("quantity", quantityAvailable);
        return jsonProduct;

    }
}
