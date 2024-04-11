package server;

import java.io.File;

public class HttpServer {

    public static final String WEB_ROOT =
            System.getProperty("user.dir") + File.separator + "webroot";
    public static final String SRC_ROOT =
            System.getProperty("user.dir") + File.separator + "src";
    public static final String SERVLET_ROOT = System.getProperty("user.dir")
            + "/src/main/java/servlet";

    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        connector.start();
    }
}
