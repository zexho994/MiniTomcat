package servlet;

import server.Request;
import server.Response;

import java.io.IOException;

public interface Servlet {
    void service(Request req, Response res) throws IOException;
}
