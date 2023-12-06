import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class loginsession {
    private String username;
    private String userType;

    UserManager userdata;
    IMarketData marketData;

    loginsession(String user, String usrType, UserManager userDb, IMarketData marketData)
    {
        username = user;
        userType = usrType;
        userdata = userDb;
        this.marketData = marketData;
    }

    void RunReq(BufferedReader in, PrintWriter out)
    {
        while(true)
        {
            Request req = Request.ReadReq(in);
            process(req, out);
        }
    }

    void process(Request req, PrintWriter out)
    {
        String reqVal = req.get("req");
        String usrVal = req.get("user");
        String pwdVal = req.get("pwd");
        String emailVal = req.get("email");
        String typeVal = req.get("type");
        boolean ret = false;
        if(reqVal.equals("removeuser")) {
            ret = userdata.removeUser(usrVal, pwdVal, typeVal);
            ReturnResult(ret?0:-1, ret?"":"remove user failed", out);
            return;
        }
        if(reqVal.equals("searchproduct"))
        {
            String keywords = req.get("keywords");

            List<productBrowseResult> result = marketData.BrowseProduct(keywords);
            ReturnproductBrowseResultArray(result, out);

            return;
        }

        if(reqVal.equals("addstore"))
        {
            String store = req.get("store");
            int err = marketData.AddStore(usrVal, store);

            ReturnResult(err, err == 0?"Add store Success":"Add store failed", out);
            return;
        }

        if(reqVal.equals("liststores"))
        {
            List<String> stores=  marketData.ListStore(usrVal);

            ReturnStringArray("string", stores, out);
            return;
        }

        if(reqVal.equals("liststoreproducts"))
        {
            String store = req.get("store");
            List<productBrowseResult> products=  marketData.ListStoreProducts(usrVal, store);

            ReturnproductBrowseResultArray(products, out);
            return;
        }

        if(reqVal.equals("addproduct"))
        {
            String store = req.get("store");
            String product = req.get("product");
            String desc = req.get("desc");
            int quantity = req.get_int("quantity");
            double price =req.get_double("price");

            int err = marketData.AddProduct(usrVal, store, product, desc, quantity, price);

            ReturnResult(err, err == 0?"Add product Success":"Add product failed", out);
        }

        if(reqVal.equals("buyproduct"))
        {
            String store = req.get("store");
            String product = req.get("product");
            int quantity = req.get_int("quantity");
            double price =req.get_double("price");

            int err = marketData.BuyProduct(usrVal, store, product, quantity, price);

            ReturnResult(err, err == 0?"Buy product Success":"Buy product failed", out);
        }

        if(reqVal.equals("removeproduct"))
        {
            String store = req.get("store");
            String product = req.get("product");

            int err = marketData.RemoveProduct(usrVal, store, product);

            ReturnResult(err, err == 0?"Remove product Success":"Add product failed", out);
        }

    }

    private void ReturnResult(int status, String msg, PrintWriter out)
    {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("status", status);
        jsonMessage.put("msg", msg);

        out.println(jsonMessage.toJSONString());
    }

    private void ReturnStringArray(String typeArray, List<String> strList, PrintWriter out)
    {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("type", typeArray);
        jsonMessage.put("array", strList);

        out.println(jsonMessage.toJSONString());
    }

    private void ReturnproductBrowseResultArray(List<productBrowseResult> prodArray, PrintWriter out)
    {
        JSONArray jarray = productBrowseResult.productsBrowseResultToJsonArray(prodArray);
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("type", "prod");
        jsonMessage.put("array", jarray);

        out.println(jsonMessage.toJSONString());
    }

}
