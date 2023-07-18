import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;


public class Request {
    private String method;
    private String path;

    public Request(String method, String path) {
        this.method = method;
        this.path = path;

    }

    public static Request RequestParser(BufferedReader in, BufferedOutputStream out) throws IOException {
        var requestLine = in.readLine();
        var split = requestLine.split(" ");
        if (split.length != 3) {
            Server.notFound(out);
        }
        return new Request(split[0], split[1]);
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

}
