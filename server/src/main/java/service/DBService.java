package service;

import dataaccess.AuthMemoryDAO;
import dataaccess.GameMemoryDAO;
import dataaccess.UserMemoryDAO;

public class DBService {

    AuthMemoryDAO authDAO;
    GameMemoryDAO gameDAO;
    UserMemoryDAO userDAO;

    public DBService(AuthMemoryDAO authDAO, GameMemoryDAO gameDAO, UserMemoryDAO userDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public void clear(){
        userDAO.clearUsers();
        gameDAO.clearGames();
        authDAO.clearAuth();
    }
}
