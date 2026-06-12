package client;

import exceptions.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;
import resultsandrequests.*;
import server.Server;

import java.util.ArrayList;

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
    public void clearTest() throws ResponseException {
        RegisterUserResult result =
                serverFacade.register(new RegisterUserRequest("a", "b", "c"));
        serverFacade.logOut(new LogOutRequest(result.authToken()));
        serverFacade.clear();
        assertThrows(ResponseException.class, () ->
                serverFacade.logIn(new LogInRequest("a", "b")));
    }

    //positive test for register User
    @Test
    public void registerUser() throws ResponseException {
        serverFacade.register(new RegisterUserRequest("Hippo", "GrassEater", "ManKiller!"));
    }

    //negative test for register User
    @Test
    public void registerUserDuplicate() throws ResponseException{
        serverFacade.register(new RegisterUserRequest("Hippo", "GrassEater", "ManKiller!"));
        assertThrows(ResponseException.class, () ->
                serverFacade.register(new RegisterUserRequest("Hippo", "Grass2Eater", "Man3Killer!")));
    }

    //positive test for login
    @Test
    public void logIn() throws ResponseException {
        serverFacade.register(new RegisterUserRequest("Jake", "Wakey", "bakeSale@"));
        serverFacade.logIn(new LogInRequest("Jake", "Wakey"));
    }

    //negative test for login
    @Test
    public void logInNotRegistered() {
        assertThrows(ResponseException.class, () ->
                serverFacade.logIn(new LogInRequest("Humpty", "cracked")));
    }

    //positive logOUt test
    @Test
    public void logOut() throws ResponseException {
        RegisterUserResult result = serverFacade.register(new RegisterUserRequest("abc", "def", "ghijk@"));
        String authToken = result.authToken();
        serverFacade.logOut(new LogOutRequest(authToken));
    }

    //negative logout test
    @Test
    public void logOutNotLoggedIn() throws ResponseException{
        assertThrows(ResponseException.class, () ->
                serverFacade.logOut(new LogOutRequest("HumptyDumpty")));
        String authToken = serverFacade.register(new RegisterUserRequest("a", "password", "cracked@")).authToken();
        serverFacade.logOut(new LogOutRequest(authToken));
        assertThrows(ResponseException.class, () ->
                serverFacade.logOut(new LogOutRequest(authToken)));
    }

    //positive list games test
    @Test
    public void listGames() throws ResponseException {
        RegisterUserResult result = serverFacade.register(new RegisterUserRequest("abc", "def", "CookieSandwich@"));
        String authToken = result.authToken();
        serverFacade.createGame(new CreateGameRequest("BuddyGame", authToken));
        ArrayList<GameData> games = serverFacade.listGames(new ListGamesRequest(authToken)).games();
        assert(games.size() == 1);
        assert(games.getFirst().gameName().equals("BuddyGame"));
    }

    //negative list games test
    @Test
    public void listGamesNotAuthorized() {
        assertThrows(ResponseException.class, () ->
                serverFacade.listGames(new ListGamesRequest("BillyJoe")));
    }

    //create game positive test
    @Test
    public void createGame() throws ResponseException {
        RegisterUserResult result = serverFacade.register(new RegisterUserRequest("jim", "crick", "liceCream@"));
        String authToken = result.authToken();
        serverFacade.createGame(new CreateGameRequest("jello", authToken));
        ArrayList<GameData> games = serverFacade.listGames(new ListGamesRequest(authToken)).games();
        assert(games.size() == 1);
        serverFacade.createGame(new CreateGameRequest("Pudding", authToken));
        games = serverFacade.listGames(new ListGamesRequest(authToken)).games();
        assert(games.getFirst().gameName().equals("jello"));
        assert(games.size() == 2);
        assert(games.getLast().gameName().equals("Pudding"));
    }

    //negative create game test
    @Test
    public void createGameNotAuthorized() {
        assertThrows(ResponseException.class, () ->
                serverFacade.createGame( new CreateGameRequest("uR", "Dad")));
    }



}
