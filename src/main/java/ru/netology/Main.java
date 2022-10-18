package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {

        final var server = new Server();

        server.addHandler("GET", "/spring.png", Main::processFile);
        server.addHandler("GET", "/spring.svg", Main::processFile);
        server.addHandler("GET", "/classic.html", Main::processFile);
        server.addHandler("GET", "/index.html", Main::processFile);
        server.addHandler("GET", "/links.html", Main::processFile);
        server.addHandler("GET", "/resources.html", Main::processFile);
        server.addHandler("GET", "/styles.css", Main::processFile);
        server.addHandler("GET", "/forms.html", Main::processFile);
        server.addHandler("GET", "/events.js", Main::processFile);
        server.addHandler("GET", "/app.js", Main::processFile);
        server.addHandler("GET", "/desktop.ini", Main::processFile);
        server.addHandler("GET", "/favicon.ico", Main::processFile);

        server.addHandler("POST", "/messages", new Handler() {
            @Override
            public void handle(Request request, BufferedOutputStream out) {

            }
        });
        server.listen(9999);
    }
    private static void processFile(Request request, BufferedOutputStream out){
        final var filePath = Path.of(".", "public", request.getPath());
        try {
            final var mimeType = Files.probeContentType(filePath);
            final var length = Files.size(filePath);
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

