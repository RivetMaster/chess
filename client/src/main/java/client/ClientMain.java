package client;

public class ClientMain {


    public static void main(String[] args) {
        var url = "http://localhost:" + 8080;
        ServerFacade server = new ServerFacade(url);
        Client client = new Client(server);

        client.scanInput();
    }
}
