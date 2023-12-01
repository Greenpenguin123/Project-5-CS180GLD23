import java.io.*;
import java.net.*;

public class MarketServer {

    static logUtility log = new logUtility("user_db.log");
    static UserManager user_data = new UserManager(log);

    public static void main(String[] args) {
        int port = 5432;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
				System.out.println("Accepted a session.");
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            } 
        }catch (IOException e) {
            e.printStackTrace();
        }        
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            // Auth
            // while (true) till a session is established
            loginsession session = null;
            while (session == null) {
                Auth auth = new Auth(MarketServer.log, MarketServer.user_data);
                // Session (buyer | seller)
                session = auth.RunReq(in, out);
            }
            session.RunReq(in, out);
        }
        catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                clientSocket.close();
                System.out.println("Connection with client closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
