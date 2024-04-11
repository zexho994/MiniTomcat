package server;

import java.io.IOException;
import java.io.InputStream;

/**
 * parse the http request
 */
public class Request {

    private String uri;
    private InputStream input;

    public Request(InputStream input) {
        this.input = input;
    }

    /**
     * parse the http request
     */
    public void parse() {
        StringBuilder request = new StringBuilder();
        byte[] buffer = new byte[2048];
        int i;
        try {
            i = input.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }
        for(int j = 0; j < i; j++){
            request.append((char) buffer[j]);
        }
        uri = parseUrl(request.toString());
    }

    public String parseUrl(String request) {
        int index1,index2;
        index1 = request.indexOf(' ');
        if(index1 != -1){
            index2 = request.indexOf(' ',index1 + 1);
            if(index2 > index1){
                return request.substring(index1 + 1,index2);
            }
        }
        return null;
    }

    public String getUri() {
        return uri;
    }
}
