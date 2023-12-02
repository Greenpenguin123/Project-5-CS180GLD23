import java.io.BufferedReader;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

public class loginsession {
    private String username;
    private String userType;

    UserManager userdata;

    loginsession(String user, String usrType, UserManager userDb)
    {
        username = user;
        userType = usrType;
        userdata = userDb;
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
        String usrVal = req.get("name");
        String pwdVal = req.get("pwd");
        String emailVal = req.get("email");
        String typeVal = req.get("type");
        boolean ret = false;
        if(reqVal.equals("removeuser"))
        {
            ret = userdata.removeUser(emailVal,pwdVal,typeVal);
            ReturnResult(ret?0:-1, ret?"":"remove user failed", out);
        }
        /*if(reqVal.equals(""))*/
    }

    private void ReturnResult(int status, String msg, PrintWriter out)
    {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("status", status);
        jsonMessage.put("msg", msg);

        out.println(jsonMessage.toJSONString());
    }

}
