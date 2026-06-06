package server;

import chess.*;
import dataaccess.UserSQLDAO;

public class ServerMain {
    public static void main(String[] args) {
        //Server server = new Server();
        //server.run(8080); //8080 port for testing
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            UserSQLDAO dataAccess = new UserSQLDAO();

            Server server = new Server(dataAccess);
            server.run(port);
            return;
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        System.out.println("♕ 240 Chess Server");
    }
}
