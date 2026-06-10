package client;

import exceptions.ResponseException;
import org.junit.jupiter.api.*;
import resultsandrequests.LogInRequest;
import resultsandrequests.RegisterUserRequest;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        serverFacade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    public void clear() throws ResponseException {
        //manually add values to db
        serverFacade.clear();
    }

    @Test
    public void registerUser() throws ResponseException {
        serverFacade.register(new RegisterUserRequest("Hippo", "GrassEater", "ManKiller!"));
        assertThrows(ResponseException.class, () ->
                serverFacade.register(new RegisterUserRequest("Hippo", "Grass2Eater", "Man3Killer!")));

    }

    @Test
    public void logIn() throws ResponseException {
        serverFacade.register(new RegisterUserRequest("Jake", "Wakey", "bakeSale@"));
        serverFacade.logIn(new LogInRequest("Jake", "Wakey"));
    }



}
