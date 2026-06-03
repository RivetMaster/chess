package service;

import dataaccess.*;

public class DBService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public DBService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public Result clear() throws DataAccessException{
        userDAO.clearUsers();
        gameDAO.clearGames();
        authDAO.clearAuth();
        return new VoidResult();
    }
}

