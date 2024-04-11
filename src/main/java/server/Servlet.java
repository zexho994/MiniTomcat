package server;

import java.io.IOException;

public interface Servlet {
    void service(Request req, Response res) throws IOException;
}
