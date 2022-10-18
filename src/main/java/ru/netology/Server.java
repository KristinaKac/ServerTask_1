package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Server {

    ExecutorService threadPool = Executors.newFixedThreadPool(64);

    public void listen(int port) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                threadPool.submit(() -> {
                    try {
                        ServerThread.working(socket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addHandler(String method, String path, Handler handler) {
       ServerThread.handlers.putIfAbsent(method, new ConcurrentHashMap<>());
       ServerThread.handlers.get(method).put(path, handler);
    }
}





