package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;

public class HttpConnector implements Runnable {

    int minProcessors = 3;
    int maxProcessors = 10;
    int curProcessors = 0;
    Deque<HttpProcessor> processors = new ArrayDeque<>();

    /**
     * 启动服务器并开始监听指定端口。
     * 此方法会创建一个服务端套接字并不断接受客户端连接，为每个连接分配一个处理器进行处理。
     * 在创建处理器和处理连接时，此方法会进行异常处理并确保资源的正确释放。
     */
    public void run() {
        ServerSocket serverSocket = null;
        int port = 8080;
        // 尝试创建一个服务器套接字并绑定到本地地址
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            // 遇到IO异常时打印堆栈跟踪并退出程序
            System.exit(1);
        }
        // 初始化处理器池
        for (int i = 0; i < minProcessors; i++) {
            HttpProcessor processor = new HttpProcessor();
            processors.push(processor);
        }
        curProcessors = minProcessors;
        // 不断接受客户端连接并处理
        while (true) {
            try {
                Socket socket = serverSocket.accept(); // 接受客户端连接
                HttpProcessor processor = createProcessor();
                // 如果无法创建处理器，则关闭连接并尝试下一个
                if (processor == null) {
                    socket.close();
                    continue;
                }
                // 使用处理器处理客户端连接，处理完成后将处理器放回池中
                processor.process(socket);
                processors.push(processor); //处理完毕后放回池子
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
                // 对于处理过程中的异常，打印堆栈跟踪
            }
        }
    }


    private HttpProcessor createProcessor() {
        synchronized (processors) {
            if (processors.size() > 0) {
                return processors.pop();
            }
            if (curProcessors < maxProcessors) {
                return (newProcessor());
            } else {
                return (null);
            }
        }
    }

    //新建一个processor
    private HttpProcessor newProcessor() {
        HttpProcessor initprocessor = new HttpProcessor();
        processors.push(initprocessor);
        curProcessors++;
        return processors.pop();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }
}
