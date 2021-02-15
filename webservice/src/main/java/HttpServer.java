
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Driver Class for the Http Server
 *
 */
public class HttpServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    static final int MAX_T = 5;

    public static void main(String[] args) throws IOException {

        ExecutorService pool = Executors.newFixedThreadPool(MAX_T);

        LOGGER.info("Server starting...");

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using WebRoot: " + conf.getWebroot());

        ServerSocket serverSocket = new ServerSocket(conf.getPort());
        while(true) {
            try {
                Socket s = serverSocket.accept();  // Wait for a client to connect
                Runnable runnable = new ClientHandlerTask(s, conf.getWebroot());  // Handle the client in a separate thread
                pool.execute(runnable);
            }
            catch (Exception x) {
                System.out.println(x.getMessage());
            }
        }
    }
}