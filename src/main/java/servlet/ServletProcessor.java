package servlet;

import org.apache.commons.lang3.text.StrSubstitutor;
import server.HttpServer;
import server.Request;
import server.Response;

import javax.servlet.Servlet;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ServletProcessor {
    //响应头定义，里面包含变量
    private static String OKMessage = "HTTP/1.1 ${StatusCode} ${StatusName}\r\n" +
            "Content-Type: ${ContentType}\r\n" + "Server: minit\r\n" +
            "Date: ${ZonedDateTime}\r\n" + "\r\n";

    public void process(Request request, Response response) {
        String uri = request.getUri();
        //首先根据uri最后一个/号来定位，后面的字符串认为是servlet名字
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        URLClassLoader loader = null;
        PrintWriter writer = null;
        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(HttpServer.SRC_ROOT);
            String repository = new URL("file", null,
                    classPath.getCanonicalPath() + File.separator).toString();
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get PrintWriter
        try {
            response.setCharacterEncoding("UTF-8");
            writer = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //由上面的URLClassLoader加载这个servlet
        Class<?> servletClass = null;
        try {
            servletClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //写响应头
        String head = composeResponseHead();
        writer.println(head);
        //创建servlet新实例，然后调用service()，由它来写动态内容到响应体 Servlet servlet = null;
        Servlet servlet = null;
        try {
            servlet = (Servlet) servletClass.newInstance();
            servlet.service(request, response);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //生成响应头，填充变量值
    private String composeResponseHead() {
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put("StatusCode", "200");
        valuesMap.put("StatusName", "OK");
        valuesMap.put("ContentType", "text/html;charset=uft-8");
        valuesMap.put("ZonedDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now()));
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String responseHead = sub.replace(OKMessage);
        return responseHead;
    }
}
