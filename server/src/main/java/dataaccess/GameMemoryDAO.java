package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class GameMemoryDAO implements GameDAO{
    ArrayList<GameData> games;
    int gameNum;

    public GameMemoryDAO(){
        games = new ArrayList<>();
        gameNum = 0;
    }

    //Create a new game.
    public void createGame(GameData game) {
        games.add(game);
        gameNum++;
    }

    //Retrieve a specified game with the given game ID.
    public GameData getGame(int gameID) throws DataAccessException{
        int index = getGameIndex(gameID);
        return games.get(index);
    }

    //Retrieve all games.
    public ArrayList<GameData> listGames() {
        return games;
    }

    //Updates a chess game. It should replace the chess game string corresponding to a given gameID.
    // This is used when players join a game or when a move is made.
    public void updateGame(int gameID, GameData game) throws DataAccessException{
        int index = getGameIndex(gameID);
        games.set(index, game);
    }

    //Add player to a game (gameID) as the team (color)
    public void addPlayer(int gameID, ChessGame.TeamColor color, String username) throws DataAccessException{
        int index = getGameIndex(gameID);
        games.set(index, games.get(index).setUser(username, color));
    }

    //clear all games
    public void clearGames(){
        games.clear();
        gameNum = 0;
    }

    //give new game ID
    public int newGameID(){
        return gameNum;
    }

    private int getGameIndex(int gameID) throws DataAccessException{
        for(int i = 0; i < games.size(); i++){
            if(games.get(i).gameID() == gameID){
                return i;
            }
        }
        throw new DataAccessException("Invalid GameID");
    }
}
