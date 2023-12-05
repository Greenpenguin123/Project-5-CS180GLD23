import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;

public class CmdIO {

    static Socket clientSocket = null;
    static PrintWriter writer;
    static BufferedReader reader;

    static JSONParser jsonParser = new JSONParser();

    public static int connect(String serverAddress, int serverPort) {
        try {
            clientSocket = new Socket(serverAddress, serverPort);
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
            return -1;
        }
    }

    static int Login(String serverAddress, int serverPort, String user, String pwd, String userType) {
        if (connect(serverAddress, serverPort) != 0) {
            return -1;
        }

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "login");
        jsonMessage.put("user", user);
        jsonMessage.put("pwd", pwd);
        jsonMessage.put("type", userType);

        writer.println(jsonMessage.toJSONString());
        try {
            String serverResponse = reader.readLine();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(serverResponse);
            System.out.println("serverResponse:" + serverResponse);

            int ret = ((Long) jsonObject.get("status")).intValue();
            return ret;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return -1;
    }

    static int AddLogin(String serverAddress, int serverPort, String user, String pwd, String userType) {
        if (connect(serverAddress, serverPort) != 0) {
            return -1;
        }

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "adduser");
        jsonMessage.put("user", user);
        jsonMessage.put("pwd", pwd);
        jsonMessage.put("type", userType);


        writer.println(jsonMessage.toJSONString());
        try {
            String serverResponse = reader.readLine();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(serverResponse);
            System.out.println("serverResponse:" + serverResponse);

            int ret = ((Long) jsonObject.get("status")).intValue();
            return ret;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return -1;
    }


    static List<String> queryStores(String seller) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "liststores");
        jsonMessage.put("user", seller);

        writer.println(jsonMessage.toJSONString());
        List<String> results = new ArrayList<>();

        try {
            String serverResponse = reader.readLine();
            System.out.println("serverResponse:" + serverResponse);

            JSONObject jsonObject = (JSONObject) jsonParser.parse(serverResponse);
            JSONArray jsonArray = (JSONArray) jsonObject.get("array");
            for (Object obj : jsonArray) {
                results.add((String) obj);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return results;
    }

    static int createStore(String seller, String storeName){
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "addstore");
        jsonMessage.put("user", seller);
        jsonMessage.put("store", storeName);
        writer.println(jsonMessage.toJSONString());
        try {
            String serverResponse = reader.readLine();
            System.out.println("serverResponse:" + serverResponse);
            JSONObject jsonObject = (JSONObject) jsonParser.parse(serverResponse);
            int ret = ((Long) jsonObject.get("status")).intValue();
            return ret;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return -1;
    }

    static int addProduct(String seller, String storeName,String product, String desc, int quantity, double price){
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "addproduct");
        jsonMessage.put("user", seller);
        jsonMessage.put("store", storeName);
        jsonMessage.put("product", product);
        jsonMessage.put("desc", desc);
        jsonMessage.put("quantity", quantity);
        jsonMessage.put("price", price);
        writer.println(jsonMessage.toJSONString());
        try {
            String serverResponse = reader.readLine();
            System.out.println("serverResponse:" + serverResponse);
            JSONObject jsonObject = (JSONObject) jsonParser.parse(serverResponse);
            int ret = ((Long) jsonObject.get("status")).intValue();
            return ret;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return -1;
    }

    static int removeProduct(String seller, String storeName,String product){
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "removeproduct");
        jsonMessage.put("user", seller);
        jsonMessage.put("store", storeName);
        jsonMessage.put("product", product);
        writer.println(jsonMessage.toJSONString());
        try {
            String serverResponse = reader.readLine();
            System.out.println("serverResponse:" + serverResponse);
            JSONObject jsonObject = (JSONObject) jsonParser.parse(serverResponse);
            int ret = ((Long) jsonObject.get("status")).intValue();
            return ret;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return -1;
    }

    static List<ProductSeller> queryStoreProduct(String seller, String storeName) {
        List<ProductSeller> products = new ArrayList<>();

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "liststoreproducts");
        jsonMessage.put("user", seller);
        jsonMessage.put("store", storeName);

        writer.println(jsonMessage.toJSONString());
        try {
            String serverResponse = reader.readLine();
            System.out.println("serverResponse:" + serverResponse);

            JSONObject jsonObject = (JSONObject) jsonParser.parse(serverResponse);
            JSONArray jsonArray = (JSONArray) jsonObject.get("array");

            for (Object obj : jsonArray) {
                JSONObject jsonProduct = (JSONObject) obj;
                String prodName = (String)jsonProduct.get("product");
                String description = (String)jsonProduct.get("description");
                int quantity = ((Long)jsonProduct.get("quantity")).intValue();
                double price = (double)jsonProduct.get("price");

                ProductSeller prod = new ProductSeller(prodName, description, quantity, price);
                products.add(prod);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return products;
    }
    /*
    static List<BrowseProduct> searchProduct(String keywords) {
        List<BrowseProduct> products = new ArrayList<>();

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "searchproduct");
        jsonMessage.put("keywords", keywords);

        writer.println(jsonMessage.toJSONString());
        try {
            String serverResponse = reader.readLine();
            System.out.println("serverResponse:" + serverResponse);

            JSONObject jsonObject = (JSONObject) jsonParser.parse(serverResponse);
            JSONArray jsonArray = (JSONArray) jsonObject.get("array");

            for (Object obj : jsonArray) {
                JSONObject jsonProduct = (JSONObject) obj;
                String seller = (String)jsonProduct.get("seller");
                String store = (String)jsonProduct.get("store");
                String prodName = (String)jsonProduct.get("product");
                String description = (String)jsonProduct.get("description");
                int quantity = ((Long)jsonProduct.get("quantity")).intValue();
                double price = (double)jsonProduct.get("price");

                BrowseProduct prod = new BrowseProduct(seller, store, prodName, description, quantity, price);
                products.add(prod);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return products;
    }*/

}
