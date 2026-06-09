package dataaccess;

import chess.ChessGame;
import model.GameData;
import server.InvalidRequestException;

import java.util.ArrayList;

public class GameMemoryDAO implements GameDAO{
    ArrayList<GameData> games;
    int gameNum;

    public GameMemoryDAO(){
        games = new ArrayList<>();
        gameNum = 0;
    }

    //Create a new game.
    public GameData createGame(GameData game) throws InvalidRequestException{
        if(!game.verifyFields()){
            throw new InvalidRequestException("Expected game name and game.");
        }
        game = game.setGameID(++gameNum);
        games.add(game);
        return game;
    }

    //Retrieve a specified game with the given game ID.
    public GameData getGame(int gameID) throws InvalidRequestException {
        int index = getGameIndex(gameID);
        return games.get(index);
    }

    //Retrieve all games.
    public ArrayList<GameData> listGames() {
        return games;
    }

    //Updates a chess game. It should replace the chess game string corresponding to a given gameID.
    // This is used when players join a game or when a move is made.
    public void updateGame(int gameID, GameData game) throws InvalidRequestException {
        int index = getGameIndex(gameID);
        game = game.setGameID(gameID);
        games.set(index, game);
    }

    //Add player to a game (gameID) as the team (color)
    public void addPlayer(int gameID, ChessGame.TeamColor color, String username) throws InvalidRequestException {
        int index = getGameIndex(gameID);
        updateGame(gameID, games.get(index).setUser(username, color));
    }

    //clear all games
    public void clearGames(){
        games.clear();
        gameNum = 0;
    }

    private int getGameIndex(int gameID) throws InvalidRequestException {
        for(int i = 0; i < games.size(); i++){
            if(games.get(i).gameID() == gameID){
                return i;
            }
        }
        throw new InvalidRequestException("Invalid GameID");
    }
}
