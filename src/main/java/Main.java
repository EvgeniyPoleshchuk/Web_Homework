


public class Main {
    public static void main(String[] args) {
        Server server = new Server();


        Server.addHandler("GET", "/spring.png", server::sendResponse);
        Server.addHandler("GET", "/index.html", server::sendResponse);
        Server.addHandler("GET","/classic.html",server::sendResponse);

        server.start(9999);
    }


}
