import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

// A ClientHandler reads an HTTP request and responds
class ClientHandlerTask implements Runnable {
    private Socket socket;  // The accepted socket from the Webserver
    private String path;
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientHandlerTask.class);
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

            // Read url from first input line "GET /url.html ..."
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
            String url = "";
            StringTokenizer st = new StringTokenizer(s);
            try {
                // Parse the url from the GET command
                if (st.hasMoreElements() && st.nextToken().equalsIgnoreCase("GET")
                        && st.hasMoreElements()){
                    url = st.nextToken();
                } else {
                    throw new FileNotFoundException();  // Bad request
                }

                String[] urlElements = url.split("\\.");
                String durationStr = urlElements[0].substring(1);

                int duration = Integer.parseInt(durationStr);
                HumidityAndTempVisualization humidityAndTempVisualization = new HumidityAndTempVisualization();
                String pngFileName = durationStr + ".png";

                if (url.endsWith(".html")) {
                    String tableHtml = humidityAndTempVisualization.humidityTempToHtml(duration);
                    String imageHtml = "<img src=\"" + pngFileName + "\" width=\"1000\">";

                    String html = "<html><head><title>Simple Java HTTP Server</title></head><body>"
                            + tableHtml + imageHtml + "</body></html>";

                    final String CRLF = "\r\n"; // 13, 10

                    String response =
                            "HTTP/1.1 200 OK" + CRLF + // Status Line  :   HTTTP_VERSION RESPONSE_CODE RESPONSE_MESSAGE
                                    "Content-Length: " + html.getBytes().length + CRLF + // HEADER
                                    CRLF +
                                    html +
                                    CRLF + CRLF;

                    out.write(response.getBytes());
                }
                else if (url.endsWith(".png")) {
                    humidityAndTempVisualization.humidityTempSaveToImage(duration, pngFileName, path);
                    InputStream f = new FileInputStream(path + pngFileName);
                    out.print("HTTP/1.0 200 OK\r\n" + "Content-type: image/png\r\n\r\n");

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
                        "<html><head></head><body>"+url+" not found</body></html>\n");
            } catch (Exception e) {
                //e.printStackTrace();
            }
            finally {
                out.close();
            }
        }
        catch (IOException x) {
            System.out.println(x);
        }
    }
}
