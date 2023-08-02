import java.util.List;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        List<String> path = List.of("/index.html","/spring.svg", "/spring.png", "/resources.html",
                "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
        for (String name:path) {
            Server.addHandler("GET", name, server::sendResponse);
        }
        Server.addHandler("GET","/default-get.html",server::sendResponse);



        server.start(9999);
    }


}
