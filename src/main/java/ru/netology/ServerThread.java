package ru.netology;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerThread {
    static String path;
    static ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();

    public static void working(Socket socket) throws IOException {

        while (true) {

            final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final var out = new BufferedOutputStream(socket.getOutputStream());

            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                continue;
            }
            Request request = new Request(parts[0], parts[1]);
            path = request.getPath();


            if (!handlers.containsKey(request.getMethod())) {
                notFound(out);
                return;
            }
            var methodHandler = handlers.get(request.getMethod());

            if (!methodHandler.containsKey(request.getPath())) {
                notFound(out);
                return;
            }
            var handler = methodHandler.get(request.getPath());
            handler.handle(request, out);
        }
    }

    public static void notFound(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }
}

