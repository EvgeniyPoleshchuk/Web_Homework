import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private static Map<String, Map<String, HandlerRequest>> handlerMap = new ConcurrentHashMap<>();

    public static void addHandler(String method, String path, HandlerRequest handler) {
        if (handlerMap.containsKey(method)) {
            handlerMap.get(method).put(path, handler);
        } else {
            Map<String, HandlerRequest> map = new ConcurrentHashMap<>();
            map.put(path, handler);
            handlerMap.put(method, map);
        }
    }

    public void start(int port) {

        ExecutorService service = Executors.newFixedThreadPool(64);

        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                service.submit(() -> {
                    logic(socket);
                });

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void logic(Socket socket) {
        try (final var out = new BufferedOutputStream(socket.getOutputStream());
             final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            while (true) {
                Request request = Request.RequestParser(in,out);
                HandlerRequest handlerRequest1 = handlerMap.get(request.getMethod()).get(request.getPath());
                handlerRequest1.handle(request, out);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void notFound(BufferedOutputStream out) {
        try {
            out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }
    public void sendResponse(Request request, BufferedOutputStream out) throws IOException {
        final Path filePath = Path.of(".", "public", request.getPath());
        final String mimeType = Files.probeContentType(filePath);

        if(!handlerMap.get(request.getMethod()).containsKey(request.getPath())){
            notFound(out);
        }
        if (request.getPath().equals("/classic.html")) {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
        }
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
    }


}



