package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import service.*;
import service.exceptions.*;
import service.resultsandrequests.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userServ;
    private final AuthService authServ;
    private final GameService gameServ;
    private final DBService dbServ;
    private final Gson serialize;

    public Server(){
        this(new AuthMemoryDAO(), new GameMemoryDAO(), new UserMemoryDAO());
    }

    public Server(UserDAO userDAO, AuthDAO authDAO){
        this(authDAO, new GameMemoryDAO(), userDAO);
    }

    public Server(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        authServ = new AuthService(authDAO);
        userServ = new UserService(authDAO, userDAO);
        gameServ = new GameService(gameDAO, authDAO);
        dbServ = new DBService(authDAO, gameDAO, userDAO);

        serialize = new Gson();

        //create javalin object, takes in config (http response and request object), if path matches file name in
        // web will return contents of that file
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::logIn)
                .delete("/session", this::logOut)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .exception(InvalidAuthTokenException.class, (Exception e, Context ctx) -> exceptionHandler(e, ctx, 401))
                .exception(InvalidLogInException.class, (Exception e, Context ctx) -> exceptionHandler(e, ctx, 401))
                .exception(DataAccessException.class, (Exception e, Context ctx) -> exceptionHandler(e, ctx, 500))
                .exception(UnavailableException.class, (Exception e, Context ctx) -> exceptionHandler(e, ctx, 403))
                .exception(InvalidRequestException.class, (Exception e, Context ctx) -> exceptionHandler(e, ctx, 400));

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    private void clear(Context ctx) throws DataAccessException {
        dbServ.clear();
    }

    private void joinGame(Context ctx) throws DataAccessException, InvalidAuthTokenException, InvalidRequestException, UnavailableException {
        //requires authorization. Gives gameID and playerColor
        String authToken = ctx.header("Authorization");
        JoinGameRequest req = serialize.fromJson(ctx.body(), JoinGameRequest.class);
        req = req.setAuth(new AuthData(authToken, authServ.getUsername(authToken)));
        if(!req.existingFields()){
            throw new InvalidRequestException("Expecting GameID, playerColor, and valid authorization.");
        }
        gameServ.joinGame(req);
    }

    private void createGame(Context ctx) throws InvalidRequestException, DataAccessException, InvalidAuthTokenException {
        //requires authorization
        String authToken = ctx.header("Authorization");
        CreateGameRequest req = serialize.fromJson(ctx.body(), CreateGameRequest.class);
        req = req.setAuthToken(authToken);
        if(!req.existingFields()){
            throw new InvalidRequestException("Expecting gameName and AuthToken.");
        }
        CreateGameResult result = gameServ.createGame(req);
        ctx.json(serialize.toJson(result));
    }

    private void listGames(Context ctx) throws InvalidRequestException, DataAccessException, InvalidAuthTokenException {
        //auth required. returns list of games
        String authToken = ctx.header("Authorization");
        ListGamesRequest req = new ListGamesRequest(authToken);
        if(!req.existingFields()){
            throw new InvalidRequestException("Expecting AuthToken");
        }
        ListGamesResult result = gameServ.listGames(req);
        ctx.json(serialize.toJson(result));
    }

    private void logOut(Context ctx) throws InvalidRequestException, DataAccessException, InvalidAuthTokenException {
        //logs out, takes in authToken, returns nothing.
        String authToken = ctx.header("Authorization");
        LogOutRequest req = new LogOutRequest(authToken);
        if(!req.existingFields()){
            throw new InvalidRequestException("Expecting AuthToken");
        }
        userServ.logOut(req);
    }

    private void logIn(Context ctx) throws InvalidRequestException, DataAccessException, InvalidLogInException {
        //Logs in an existing user (takes in user, password), (returns a new authToken)
        LogInRequest req = serialize.fromJson(ctx.body(), LogInRequest.class);
        if(!req.existingFields()){
            //invalid request if missing fields or just whitespace
            throw new InvalidRequestException("Expecting username and password.");
        }
        LogInResult result = userServ.logIn(req);
        ctx.json(serialize.toJson(result)); //return authToken
    }

    private void register(Context ctx) throws UnavailableException, DataAccessException, InvalidRequestException, InvalidLogInException {
        //should be given user, password, and email
        RegisterUserRequest req = serialize.fromJson(ctx.body(), RegisterUserRequest.class);
        if(!req.existingFields()){
            //invalid request if missing field or just whitespace
            throw new InvalidRequestException("Expecting username, password, and email.");
        }
        RegisterUserResult result = userServ.register(req);
        ctx.json(serialize.toJson(result));
    }

    private void exceptionHandler(Exception e, Context ctx, int stat) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        ctx.status(stat);
        ctx.json(body);
    }

    public void stop() {
        javalin.stop();
    }
}
