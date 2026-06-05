package service;

import dataaccess.*;
import service.resultsandrequests.*;

public class DBService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public DBService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public VoidResult clear() throws DataAccessException{
        userDAO.clearUsers();
        gameDAO.clearGames();
        authDAO.clearAuth();
        return new VoidResult();
    }
}

