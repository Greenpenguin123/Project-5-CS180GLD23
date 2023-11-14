public class StoreSales {
    private String storeName;
    private int totalItemsSold;

    private double totalRevenue;

    public StoreSales(String storeName) {
        this.storeName = storeName;
        this.totalItemsSold = 0;
        this.totalRevenue = 0.0;
    }

    public void addItemsSold(int itemsSold) {
        this.totalItemsSold += itemsSold;
    }

    public void addTotalRevenue(double totalRevenue) {
        this.totalRevenue += totalRevenue;
    }

    public String getStoreName() {
        return storeName;
    }

    public int getTotalItemsSold() {
        return totalItemsSold;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}
