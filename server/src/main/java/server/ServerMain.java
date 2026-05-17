package server;

import chess.*;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080); //8080 port for testing

        System.out.println("♕ 240 Chess Server");
    }
}
