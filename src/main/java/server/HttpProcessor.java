package server;

import servlet.ServletProcessor;
import servlet.StaticsResourceProcessor;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor {

    public HttpProcessor() {
    }

    public void process(Socket socket) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
            // create Request object and parse
            Request request = new Request(input);
            request.parse();
            // create Response object
            Response response = new Response(output);
            response.setRequest(request);
            // check if this is a request for a servlet or a static resource
            // a request for a servlet begins with "/servlet/"
            if (request.getUri().startsWith("/servlet/")) {
                ServletProcessor processor = new ServletProcessor();
                processor.process(request, response);
            } else {
                StaticsResourceProcessor processor = new StaticsResourceProcessor();
                processor.process(request, response);
            }
            // Close the socket
            //socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
