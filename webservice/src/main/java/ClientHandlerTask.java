import com.amazonaws.services.dynamodbv2.xspec.S;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.StringTokenizer;

// A ClientHandler reads an HTTP request and responds
class ClientHandlerTask implements Runnable {
    final private Socket socket;  // The accepted socket from the Webserver
    final private String path;
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientHandlerTask.class);
    final static String CRLF = "\r\n";

    // Start the thread in the constructor
    public ClientHandlerTask(Socket s, String path) {
        socket = s;
        this.path = path;
    }

    @Override// Read the HTTP request, respond, and close the connection
    public void run() {
        try {
            LOGGER.info("id of the thread is " + Thread.currentThread().getId());
            // Open connections to the socket
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));

            // Read path from first input line "GET /path.html ..."
            // or if not in this format, treat as a file not found.
            String s = in.readLine();
            if (s == null) {
                out.close();
                return;
            }
            System.out.println(s);  // Log the request

            // Attempt to serve the file.  Catch FileNotFoundException and
            // return an HTTP error "404 Not Found".  Treat invalid requests
            // the same way.
            String path = "";
            StringTokenizer st = new StringTokenizer(s);
            try {
                // Parse the path from the GET command
                if (st.hasMoreElements() && st.nextToken().equalsIgnoreCase("GET")
                        && st.hasMoreElements()){
                    path = st.nextToken();
                    //System.out.println("path: " + path);
                } else {
                    throw new FileNotFoundException();  // Bad request
                }

                path = path.substring(1);

                HumidityAndTempVisualization humidityAndTempVisualization = new HumidityAndTempVisualization();
                //path will be like: /data?starttime=xxx&endtime=yyy
                if (path.startsWith("data")) {

                    String query = path.split("\\?")[1];
                    String startTimeInfo = query.split("&")[0].split("=")[1];
                    String endTimeInfo = query.split("&")[1].split("=")[1];
                    double startTime = Double.parseDouble(startTimeInfo);
                    double endTime = Double.parseDouble(endTimeInfo);
                    System.out.println("startTime: " + startTime);
                    System.out.println("endTime: " + endTime);
                    String json = humidityAndTempVisualization.humidityTempToJson(startTime, endTime);

                    String response =
                            "HTTP/1.1 200 OK" + CRLF +
                                    "Content-Length: " + json.getBytes().length + CRLF +
                                    CRLF +
                                    json +
                                    CRLF + CRLF;
                    out.write(response.getBytes());
                } else {
                    System.out.println("path: " + path);
                    InputStream f = new FileInputStream("webservice/static/"+path);
                    out.print("HTTP/1.0 200 OK" + CRLF + CRLF);
                    byte[] a = new byte[4096];
                    int n;
                    while ((n = f.read(a))>0){
                        out.write(a, 0, n);
                    }
                }
            }
            catch (FileNotFoundException x) {
                out.println("HTTP/1.0 404 Not Found\r\n"+
                        "Content-type: text/html\r\n\r\n"+
                        "<html><head></head><body>"+path+" not found</body></html>\n");
            } catch (Exception e) {
                //e.printStackTrace();
            }
            finally {
                out.close();
            }
        } catch (IOException x) {
            System.out.println(x.getMessage());
        }
    }
}
