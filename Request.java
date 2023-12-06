import java.io.*;
import java.net.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Request {
    // member
    private JSONObject jsonObj = null;
    // method
    Request(JSONObject jsonreq)
    {
        jsonObj = jsonreq;
    }

    String get(String property)
    {
        if(jsonObj.containsKey(property))
        {
            return (String)jsonObj.get(property);
        }

        return null;
    }

    int get_int(String property)
    {
        if(jsonObj.containsKey(property))
        {
            try{
                //String val = jsonObj.get(property);
                int iVal = ((Long)jsonObj.get(property)).intValue();

                return iVal;
            } catch(Exception e)
            {
                e.printStackTrace();;
            }
        }

        return 0;
    }

    double get_double(String property)
    {
        if(jsonObj.containsKey(property))
        {
            return ((double)jsonObj.get(property));
        }

        return 0.0;
    }

    static Request ReadReq(BufferedReader in)
    {
        JSONParser jsonParser = new JSONParser();

        try {
            String inputLine = in.readLine();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(inputLine);

            return new Request(jsonObject);
        }
        catch(ParseException | IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
