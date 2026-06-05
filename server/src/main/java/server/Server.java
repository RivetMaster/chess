package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.AuthService;
import service.DBService;
import service.GameService;
import service.UserService;
import service.exceptions.InvalidAuthTokenException;
import service.exceptions.InvalidLogInException;
import service.exceptions.UnavailableException;
import service.resultsandrequests.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userServ;
    private final AuthService authServ;
    private final GameService gameServ;
    private final DBService dbServ;
    private final Gson serialize;

    public Server() {
        AuthDAO authDAO = new AuthMemoryDAO();
        GameDAO gameDAO = new GameMemoryDAO();
        UserDAO userDAO = new UserMemoryDAO();
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

    private void logOut(Context ctx) throws InvalidRequestException, DataAccessException, InvalidAuthTokenException {
        //logs out, takes in authToken, returns nothing.
        String authToken = ctx.header("Authorization");
        Map<String, String> headers = ctx.headerMap();
        logOutRequest req = new logOutRequest(authToken);
        if(!req.existingFields()){
            throw new InvalidRequestException("Expecting AuthToken");
        }
        userServ.logOut(req);
        ctx.status(200);
    }

    private void logIn(Context ctx) throws InvalidRequestException, DataAccessException, InvalidLogInException {
        //Logs in an existing user (takes in user, password), (returns a new authToken)
        logInRequest req = serialize.fromJson(ctx.body(), logInRequest.class);
        if(!req.existingFields()){
            //invalid request if missing fields or just whitespace
            throw new InvalidRequestException("Expecting username and password.");
        }
        logInResult result = userServ.logIn(req);
        ctx.json(serialize.toJson(result)); //return authToken
    }

    private void register(Context ctx) throws UnavailableException, DataAccessException, InvalidRequestException, InvalidLogInException {
        //should be given user, password, and email
        registerUserRequest req = serialize.fromJson(ctx.body(), registerUserRequest.class);
        if(!req.existingFields()){
            //invalid request if missing field or just whitespace
            throw new InvalidRequestException("Expecting username, password, and email.");
        }
        registerUserResult result = userServ.register(req);
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
