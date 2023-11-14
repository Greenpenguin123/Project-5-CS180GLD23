public class StorePurchaseData {
    private String storeName;
    private int purchaseCount;

    public StorePurchaseData(String storeName) {
        this.storeName = storeName;
        this.purchaseCount = 1;
    }

    public void incrementPurchaseCount() {
        this.purchaseCount++;
    }

    public String getStoreName() {
        return storeName;
    }

    public int getPurchaseCount() {
        return purchaseCount;
    }
}