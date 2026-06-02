package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.List;

public interface GameDAO {
//createGame: Create a new game.
    void createGame(GameData game);
//getGame: Retrieve a specified game with the given game ID.
    GameData getGame(int gameID);
//listGames: Retrieve all games.
    List<GameData> listGames();
//updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID.
// This is used when players join a game or when a move is made.
    void updateGame(int gameID, ChessGame game);
    void addPlayer(int gameID, ChessGame.TeamColor color, String username);

    void clearGames();
}
