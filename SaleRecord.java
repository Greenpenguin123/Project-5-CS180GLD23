import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.*;
import java.net.*;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaleRecord {
    public String buyer;
    public LocalDateTime datetime;
    public String sellerName;
    public String storeName;
    public String productName;
    public int quantity;
    public double price;

    public SaleRecord(String buyer, LocalDateTime datetime, String sellerName, String storeName, String productName, int quantity, double price) {
        this.buyer = buyer;
        this.datetime = datetime;
        this.sellerName = sellerName;
        this.storeName = storeName;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public static JSONArray salesToJsonArray(List<SaleRecord> saleRecords) {
        JSONArray jSaleRecords = new JSONArray();

        for (SaleRecord saleRecord : saleRecords) {
            JSONObject salesJson = new JSONObject();
            salesJson.put("buyer", saleRecord.buyer);
            salesJson.put("datetime", saleRecord.datetime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            salesJson.put("seller", saleRecord.sellerName);
            salesJson.put("store", saleRecord.storeName);
            salesJson.put("product", saleRecord.productName);
            salesJson.put("quantity", saleRecord.quantity);
            salesJson.put("price", saleRecord.price);

            jSaleRecords.add(salesJson);
        }

        return jSaleRecords;
    }
}