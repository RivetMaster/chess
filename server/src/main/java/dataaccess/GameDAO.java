package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public interface GameDAO {
    //Create a new game.
    void createGame(GameData game) throws DataAccessException;
    //Retrieve a specified game with the given game ID.
    GameData getGame(int gameID) throws DataAccessException;
    //Retrieve all games.
    List<GameData> listGames() throws DataAccessException;
    //Updates a chess game. It should replace the chess game string corresponding to a given gameID.
        // This is used when players join a game or when a move is made.
    void updateGame(int gameID, ChessGame game) throws DataAccessException;
    //Add player to a game (gameID) as the team (color)
    void addPlayer(int gameID, ChessGame.TeamColor color, String username) throws DataAccessException;
    //clear all games
    void clearGames();
}
