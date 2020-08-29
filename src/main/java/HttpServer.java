
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * Driver Class for the Http Server
 *
 */
public class HttpServer {
    private static ServerSocket serverSocket;
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    public static void main(String[] args) throws IOException {

        LOGGER.info("Server starting...");

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using WebRoot: " + conf.getWebroot());

        serverSocket = new ServerSocket(conf.getPort());
        while (true) {
            try {
                Socket s = serverSocket.accept();  // Wait for a client to connect
                new ClientHandler(s, conf.getWebroot());  // Handle the client in a separate thread
            }
            catch (Exception x) {
                System.out.println(x);
            }
        }

    }

}