package server;

import servlet.ServletProcessor;
import servlet.StaticsResourceProcessor;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor implements Runnable {

    private Socket socket;
    private boolean available = false;
    private HttpConnector connector;

    public HttpProcessor(HttpConnector connector) {
        this.connector = connector;
    }

    public void process(Socket socket) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        InputStream input = null;
        OutputStream output = null;

        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
            // 创建请求对象并解析
            Request request = new Request(input);
            request.parse();
            // 创建响应对象
            Response response = new Response(output);
            response.setRequest(request);
            response.sendStaticResource();
            // 检查这是对servlet还是静态资源的请求
            // a request for a servlet begins with "/servlet/"
            if (request.getUri().startsWith("/servlet/")) {
                ServletProcessor processor = new ServletProcessor();
                processor.process(request, response);
            } else {
                StaticsResourceProcessor processor = new StaticsResourceProcessor();
                processor.process(request, response);
            }
            // 关闭 socket
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            // 等待socket分配过来
            Socket socket = await();
            if (socket == null) continue; // 处理请求
            process(socket);
            // 回收processor
            connector.recycle(this);
        }
    }

    synchronized void assign(Socket socket) { // 等待connector提供一个新的socket
        while (available) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        // 获取到这个新的
        this.socket = socket; // 把标志设置回去
        available = true; //通知另外的线程
        notifyAll();
    }

    private synchronized Socket await() {
        // 等待connector提供一个新的socket
        while (!available) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        // 获得这个新的Socket
        Socket socket = this.socket; //设置标志为false
        available = false; //通知另外的线程

        notifyAll();
        return (socket);
    }

}
