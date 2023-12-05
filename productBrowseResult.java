
import java.io.*;
import java.net.*;
import java.util.List;

import org.json.simple.*;

public class productBrowseResult {
    private String seller;
    private String store;
    private String product;
    private String description;
    private int quantity;
    private double price;

    public productBrowseResult(String seller, String store, String product, String description, int quantity,
                               double price) {
        this.seller = seller;
        this.store = store;
        this.product = product;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }

    public String toJson() {
        JSONObject json = new JSONObject();
        json.put("seller", seller);
        json.put("store", store);
        json.put("product", product);
        json.put("description", description);
        json.put("quantity", quantity);
        json.put("price", price);
        return json.toJSONString();
    }

    public JSONObject toJsonObj() {
        JSONObject json = new JSONObject();
        json.put("seller", seller);
        json.put("store", store);
        json.put("product", product);
        json.put("description", description);
        json.put("quantity", quantity);
        json.put("price", price);
        return json;
    }

    public static JSONArray productsBrowseResultToJsonArray(List<productBrowseResult> products) {
        JSONArray productList = new JSONArray();

        for (productBrowseResult product : products) {
            JSONObject json = product.toJsonObj();
            productList.add(json);
        }

        return productList;
    }

};