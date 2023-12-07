import java.io.*;
import java.net.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Test {

    public static void main(String[] args) {
        try {
            String serverAddress = "localhost";
            int serverPort = 5432;

            Socket clientSocket = new Socket(serverAddress, serverPort);
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Send login
            adduser(writer, reader, "Wallen", "buyer");
            login(writer, reader, "Wallen", "pwdeee", "buyer");
            //removelogin(writer, reader, "smith1");
            //AddStore(writer, reader, "Smith", "fishing store");
            //AddProduct(writer, reader, "Smith", "fishing store", "UnderWatertCamera", "EIOUp", 10, 84.99);
            BuyProduct(writer, reader, "joe", "joe's 4th store", "Rose", 4, 5.99);
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void adduser(PrintWriter writer, BufferedReader reader, String user, String typeuser)
    {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "adduser");
        jsonMessage.put("user", user);
        jsonMessage.put("pwd", "pwdeee");
        jsonMessage.put("email", "user@foo.org");
        jsonMessage.put("type", typeuser);

        writer.println(jsonMessage.toJSONString());
        try{
            String serverResponse = reader.readLine();
            System.out.println("serverResponse:" + serverResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void login(PrintWriter writer, BufferedReader reader, String user, String pwd, String usrType)
    {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "login");
        jsonMessage.put("user", user);
        jsonMessage.put("pwd", pwd);
        jsonMessage.put("type", usrType);

        writer.println(jsonMessage.toJSONString());
        try{
            String serverResponse = reader.readLine();
            System.out.println("serverResponse:" + serverResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void removelogin(PrintWriter writer, BufferedReader reader, String user)
    {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "removeuser");
        jsonMessage.put("user", user);

        writer.println(jsonMessage.toJSONString());
        try{
            String serverResponse = reader.readLine();
            System.out.println("serverResponse:" + serverResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void AddStore(PrintWriter writer, BufferedReader reader, String user, String storeName)
    {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "addstore");
        jsonMessage.put("user", user);
        jsonMessage.put("store", storeName);

        writer.println(jsonMessage.toJSONString());

        try{
            String serverResponse = reader.readLine();
            System.out.println("serverResponse:" + serverResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void AddProduct(PrintWriter writer, BufferedReader reader, String user, String storeName, String product, String desc, int quantity, double price)
    {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "addproduct");
        jsonMessage.put("user", user);
        jsonMessage.put("store", storeName);
        jsonMessage.put("product", product);
        jsonMessage.put("desc", desc);
        jsonMessage.put("quantity", quantity);
        jsonMessage.put("price", price);

        writer.println(jsonMessage.toJSONString());
        try{
            String serverResponse = reader.readLine();
            System.out.println("serverResponse:" + serverResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void BuyProduct(PrintWriter writer, BufferedReader reader, String user, String storeName, String product, int quantity, double price)
    {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("req", "buyproduct");
        jsonMessage.put("user", user);
        jsonMessage.put("store", storeName);
        jsonMessage.put("product", product);
        jsonMessage.put("quantity", quantity);
        jsonMessage.put("price", price);

        writer.println(jsonMessage.toJSONString());
        try{
            String serverResponse = reader.readLine();
            System.out.println("serverResponse:" + serverResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

