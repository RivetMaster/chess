package dataaccess;

import chess.ChessGame;
import model.GameData;
import server.InvalidRequestException;

import java.util.ArrayList;

public interface GameDAO {
    //Create a new game.
    GameData createGame(GameData game) throws DataAccessException, InvalidRequestException;
    //Retrieve a specified game with the given game ID.
    GameData getGame(int gameID) throws DataAccessException, InvalidRequestException;
    //Retrieve all games.
    ArrayList<GameData> listGames() throws DataAccessException;
    //Updates a chess game. It should replace the chess game string corresponding to a given gameID.
        // This is used when players join a game or when a move is made.
    void updateGame(int gameID, GameData game) throws DataAccessException, InvalidRequestException;
    //Add player to a game (gameID) as the team (color)
    void addPlayer(int gameID, ChessGame.TeamColor color, String username) throws DataAccessException, InvalidRequestException;
    //clear all games
    void clearGames() throws DataAccessException;
}
