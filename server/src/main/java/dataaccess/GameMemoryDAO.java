package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameMemoryDAO implements GameDAO{
    ArrayList<GameData> games;

    public GameMemoryDAO(){
        games = new ArrayList<>();
    }

    //Create a new game.
    public void createGame(GameData game) {
        games.add(game);
    }

    //Retrieve a specified game with the given game ID.
    public GameData getGame(int gameID) throws DataAccessException{
        int index = getGameIndex(gameID);
        return games.get(index);
    }

    //Retrieve all games.
    public List<GameData> listGames() {
        return games;
    }

    //Updates a chess game. It should replace the chess game string corresponding to a given gameID.
    // This is used when players join a game or when a move is made.
    public void updateGame(int gameID, ChessGame game) throws DataAccessException{
        int index = getGameIndex(gameID);
        games.set(index, games.get(index).setGame(game));
    }

    //Add player to a game (gameID) as the team (color)
    public void addPlayer(int gameID, ChessGame.TeamColor color, String username) throws DataAccessException{
        int index = getGameIndex(gameID);
        games.set(index, games.get(index).setUser(username, color));
    }

    //clear all games
    public void clearGames(){
        games.clear();
    }

    private int getGameIndex(int gameID) throws DataAccessException{
        for(int i = 0; i < games.size(); i++){
            if(games.get(i).gameID() == gameID){
                return i;
            }
        }
        throw new DataAccessException("Game Does Not Exist");
    }
}
