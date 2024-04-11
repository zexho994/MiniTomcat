package server;

import servlet.ServletProcessor;
import servlet.StaticsResourceProcessor;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor {

    public void process(Socket socket) {
        try {
            // parse inputStream to request
            InputStream input = socket.getInputStream();
            Request request = new Request(input);
            request.parse();

            OutputStream output = socket.getOutputStream();
            Response response = new Response(output);
            response.setRequest(request);

            if (request.getUri().startsWith("/servlet/")) {
                ServletProcessor processor = new ServletProcessor();
                processor.process(request, response);
            } else {
                StaticsResourceProcessor processor = new StaticsResourceProcessor();
                processor.process(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
