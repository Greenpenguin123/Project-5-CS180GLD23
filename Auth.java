import java.io.*;
import java.net.*;

import org.json.simple.JSONObject;

public class Auth {
    private ILog log;
    UserManager userData;
    IMarketData marketData;

    Auth(ILog ilog, UserManager userdata, IMarketData marketData)
    {
        log = ilog;
        this.userData = userdata;
        this.marketData = marketData;
    }

    loginsession RunReq(BufferedReader in, PrintWriter out)
    {
        // Read req
        // Request req <- read (in)
        Request req = Request.ReadReq(in);

        // process req
        // process req -> out

        loginsession session = process(req, out);

        return session;
    }

    loginsession process(Request req, PrintWriter out )
    {
        String reqVal = req.get("req");
        String usrVal = req.get("user");
        String pwdVal = req.get("pwd");
        String typeVal = req.get("type");
        boolean ret = false;
        if(reqVal.equals("adduser"))
        {

            ret = userData.addUser(usrVal, pwdVal, typeVal);
            JSONObject jsonMessage = new JSONObject();
            ReturnResult(ret?0:-1, ret?"":"Add user failed", out);
            return null;
        }

        if(reqVal.equals("login"))
        {
            ret = userData.verifyUser(usrVal, pwdVal, typeVal);
            ReturnResult(ret?0:-2, ret?"":"login user failed", out);
            if(ret)
            {
                return new loginsession(usrVal, typeVal, userData, marketData);
            }
        }

        return null;
    }

    private void ReturnResult(int status, String msg, PrintWriter out)
    {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("status", status);
        jsonMessage.put("msg", msg);

        out.println(jsonMessage.toJSONString());
    }
}
