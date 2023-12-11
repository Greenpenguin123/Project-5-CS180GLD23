import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
import static org.junit.Assert.assertEquals;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.json.simple.JSONObject;
import org.junit.Before;

public class Test {

    private StringWriter mockOutput;
    private BufferedReader mockInput;
    private PrintWriter mockWriter;

    @Before
    public void setUp() {
        mockOutput = new StringWriter();
        mockInput = new BufferedReader(new StringReader(""));
        mockWriter = new PrintWriter(mockOutput, true);
    }

    @org.junit.Test
    public void testAddUser() throws IOException {
        JSONObject expectedJsonRequest = new JSONObject();
        expectedJsonRequest.put("req", "adduser");
        expectedJsonRequest.put("user", "Wallen");
        expectedJsonRequest.put("pwd", "pwdeee");
        expectedJsonRequest.put("email", "user@foo.org");
        expectedJsonRequest.put("type", "buyer");

        assertEquals("", mockOutput.toString().trim());
    }

    @org.junit.Test
    public void testLogin() throws IOException {
        JSONObject expectedJsonRequest = new JSONObject();
        expectedJsonRequest.put("req", "login");
        expectedJsonRequest.put("user", "Wallen");
        expectedJsonRequest.put("pwd", "pwdeee");
        expectedJsonRequest.put("type", "buyer");

        assertEquals("", mockOutput.toString().trim());
    }

    @org.junit.Test
    public void testRemoveLogin() throws IOException {
        JSONObject expectedJsonRequest = new JSONObject();
        expectedJsonRequest.put("req", "removeuser");
        expectedJsonRequest.put("user", "smith1");

        assertEquals("", mockOutput.toString().trim());
    }

    @org.junit.Test
    public void testAddStore() throws IOException {
        JSONObject expectedJsonRequest = new JSONObject();
        expectedJsonRequest.put("req", "addstore");
        expectedJsonRequest.put("user", "Smith");
        expectedJsonRequest.put("store", "fishing store");

        assertEquals("", mockOutput.toString().trim());
    }

    @org.junit.Test
    public void testAddProduct() throws IOException {
        JSONObject expectedJsonRequest = new JSONObject();
        expectedJsonRequest.put("req", "addproduct");
        expectedJsonRequest.put("user", "Smith");
        expectedJsonRequest.put("store", "fishing store");
        expectedJsonRequest.put("product", "UnderWatertCamera");
        expectedJsonRequest.put("desc", "EIOUp");
        expectedJsonRequest.put("quantity", 10);
        expectedJsonRequest.put("price", 84.99);

        assertEquals("", mockOutput.toString().trim());
    }

    @org.junit.Test
    public void testBuyProduct() throws IOException {
        JSONObject expectedJsonRequest = new JSONObject();
        expectedJsonRequest.put("req", "buyproduct");
        expectedJsonRequest.put("user", "joe");
        expectedJsonRequest.put("store", "joe's 4th store");
        expectedJsonRequest.put("product", "Rose");
        expectedJsonRequest.put("quantity", 4);
        expectedJsonRequest.put("price", 5.99);

        assertEquals("", mockOutput.toString().trim());


    }
    @org.junit.Test
    public void testSearchProduct() throws IOException {
        String keywords = "keyword1 keyword2";
        JSONObject expectedJsonRequest = new JSONObject();
        expectedJsonRequest.put("req", "searchproduct");
        expectedJsonRequest.put("keywords", keywords);
        assertEquals("", mockOutput.toString().trim());

    }

}
