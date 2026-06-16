package client;

import chess.ChessGame;
import exceptions.ResponseException;
import resultsandrequests.UIResponse;
import ui.ClientUI;
import static client.Client.State.*;
import static ui.EscapeSequences.RESET_TEXT_BOLD_FAINT;
import static ui.EscapeSequences.SET_TEXT_BOLD;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Client {
    private State state;
    private static ChessClient chessClient;
    private String authToken;
    private String username;
    private int gameID;

    public enum State {
        SIGNED_OUT,
        SIGNED_IN,
        PLAYING_GAME,
        WATCHING_GAME
    }

    public Client(ServerFacade server){
        try {
            chessClient = new ChessClient(server);
        } catch(ResponseException e){
            System.out.printf("Unable to start server: %s%n", e.getMessage());
        }
    }


    public void scanInput(){
        System.out.println("♕ Welcome to the 240 Chess Client. Type "+ SET_TEXT_BOLD+ "HELP"
                + RESET_TEXT_BOLD_FAINT + " for help getting started. ♕" );

        boolean testing = true;
        Scanner scanner = new Scanner(System.in);
        state = SIGNED_OUT;

        //loop with scanner object
        while(true){
            System.out.print(">>>> ");
            String line = scanner.nextLine().trim();
            StringBuilder output = new StringBuilder();

            //split line into words
            String[] words = line.split(" ");

            //if nothing was entered
            if(words.length == 0){
                output.append("Type ").append(ClientUI.bold(  "HELP")).append(" to see a list of valid commands.");
            }
            //QUIT when signed out
            else if(state == SIGNED_OUT &&
                    (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("q"))){
                break;
            }
            //help
            else if(line.equalsIgnoreCase("help") || line.equalsIgnoreCase("h")){
                output.append(ClientUI.helpMenu(state));
            }
            //clear (TESTING only)
            else if(testing && line.equalsIgnoreCase("clear")){
                output.append(chessClient.clear());
                state = SIGNED_OUT;
            }
            //Signed out options
            else {
                switch(state) {
                    case SIGNED_OUT -> output.append(signedOutREPL(words));
                    case SIGNED_IN -> output.append(signedInREPL(words));
                    case WATCHING_GAME, PLAYING_GAME -> output.append(inGameREPL(words));
                }
            }
            //if output comes back empty
            if(output.isEmpty()){
                //invalid command
                output.append("Type ").append(ClientUI.bold(  "HELP")).append(" to see a list of valid commands.");
            }
            System.out.println(output);
        }
    }

    private String signedOutREPL(String[] words){

        StringBuilder reply = new StringBuilder();
        String command  = words[0].toLowerCase();

        switch(command) {
            //REGISTER
            case("register") -> {
                if (words.length != 4) {
                    reply.append("Expecting ").append(ClientUI.bold("register <USERNAME> <PASSWORD> <EMAIL>"));
                } else {
                    UIResponse response = chessClient.register(words[1], words[2], words[3]);
                    reply.append(response.message());
                    if (success(response.authToken())) {
                        authToken = response.authToken();
                        state = SIGNED_IN;
                        username = words[1];
                    }
                }
            }
            //LOGIN
            case("login") -> {
                if (words.length != 3) {
                    reply.append("Expecting ").append(ClientUI.bold("login <USERNAME> <PASSWORD>"));
                } else {
                    UIResponse response = chessClient.login(words[1], words[2]);
                    reply.append(response.message());
                    if (success(response.authToken())) {
                        authToken = response.authToken();
                        state = SIGNED_IN;
                        username = words[1];
                    }
                }
            }
        }
        return reply.toString();
    }

    private String signedInREPL(String[] words){
        StringBuilder reply = new StringBuilder();
        String command = words[0].toLowerCase();

        switch(command) {
            //LOGOUT
            case("logout") -> {
                if (words.length != 1) {
                    reply.append("Expecting ").append(ClientUI.bold("logout"));
                } else {
                    UIResponse response = chessClient.logout(authToken);
                    reply.append(response.message());
                    if (!success(response.authToken())) {
                        authToken = null;
                        state = SIGNED_OUT;
                        username = null;
                    }
                }
            }
            //CREATE GAME
            case("create") -> {
                if (words.length != 2) {
                    reply.append("Expecting ").append(ClientUI.bold("create <NAME>"));
                } else {
                    String response = chessClient.create(words[1], authToken);
                    reply.append(response);
                }
            }
            //LIST GAMES
            case("list") -> {
                if (words.length != 1) {
                    reply.append("Expecting ").append(ClientUI.bold("list"));
                } else {
                    String response = chessClient.list(authToken);
                    reply.append(response);
                }
            }
            // JOIN GAME
            case("join") -> {
                if (words.length != 3) {
                    reply.append("Expecting ").append(ClientUI.bold("join <ID> <COLOR>"));
                } else {
                    int id = 0;
                    ChessGame.TeamColor color = null;
                    try {
                        //parse gameID and color
                        id = Integer.parseInt(words[1]);
                        color = ChessGame.TeamColor.valueOf(words[2].toUpperCase());
                    } catch (NumberFormatException e) {
                        reply.append(ClientUI.red("Error: Invalid Game ID"));
                    } catch (IllegalArgumentException e) {
                        reply.append(ClientUI.red("Error: Invalid color. Expecting " + ClientUI.bold("WHITE")
                                + " or " + ClientUI.bold("BLACK") + "."));
                    }
                    UIResponse response = chessClient.join(id, color, authToken);
                    reply.append(response.message());
                    if (success(response.authToken())) {
                        authToken = response.authToken();
                        state = PLAYING_GAME;
                        chessClient.connect(authToken, id);
                        gameID = id;
                    }
                }
            }
            //OBSERVE GAME
            case("observe") -> {
                if (words.length != 2) {
                    reply.append("Expecting ").append(ClientUI.bold("observe <ID>"));
                } else {
                    int id = 0;
                    try {
                        id = Integer.parseInt(words[1]);
                    } catch (NumberFormatException e) {
                        reply.append(ClientUI.red("Error: Invalid Game ID"));
                    }
                    UIResponse response = chessClient.observe(id, authToken);
                    reply.append(response.message());
                    if (success(response.authToken())) {
                        authToken = response.authToken();
                        state = WATCHING_GAME;
                        chessClient.connect(authToken, id);
                        gameID = id;
                    }
                }
            }
        }
        return reply.toString();
    }

    private String inGameREPL(String[] words){
        StringBuilder reply = new StringBuilder();
        String command = words[0].toLowerCase();

        //LEAVE
        switch(command) {
            case("leave") -> {
                if (words.length != 1) {
                    reply.append("Expecting ").append(ClientUI.bold("leave"));
                } else {
                    //Remove from game, allowing other people to join game
                    UIResponse response = chessClient.leaveGame(authToken, gameID);
                    reply.append(response.message());
                    if(success(response.authToken())) {
                        state = SIGNED_IN;
                        gameID = 0;
                    }
                }
            }
            //REDRAW
            case ("redraw") -> {
                if (words.length != 1) {
                    reply.append("Expecting ").append(ClientUI.bold("redraw"));
                } else {
                    //query server for the board, server should have list of what game they're in
                    reply.append(chessClient.redrawBoard(gameID, authToken));
                }
            }
            //MOVE
            case("move") -> {
                if (state == PLAYING_GAME && words.length != 3) {
                    reply.append("Expecting ").append(ClientUI.bold("move <Starting Position> <Ending Position>"));
                } else if (state == PLAYING_GAME){
                    if (Pattern.matches("[a-h][1-8]", words[1].toLowerCase()) &&
                            Pattern.matches("[a-h][1-8]", words[2].toLowerCase())) {
                        //check moves to see if valid, starting a-h
                        chessClient.makeMove(authToken, gameID, words[1], words[2]);
                        reply.append("\n");
                    } else {
                        reply.append(ClientUI.red("Error: Invalid move format."));
                    }
                }
            }
            //RESIGN
            case("resign") -> {
                if (state == PLAYING_GAME) {
                    if (words.length != 1) {
                        reply.append("Expecting ").append(ClientUI.bold("resign"));
                    } else {
                        UIResponse response = chessClient.resign(authToken, gameID);
                        reply.append(response.message());
                    }
                }
            }
            //HIGHLIGHT legal moves
            case("highlight") -> {
                if (words.length != 2) {
                    reply.append("Expecting ").append(ClientUI.bold("highlight <Piece Position>"));
                } else {
                    if (Pattern.matches("[a-h][1-8]", words[1])) {
                        reply.append(chessClient.highlightBoard(gameID, authToken, chessClient.toPos(words[1])));
                    } else {
                        reply.append(ClientUI.red("Error: Invalid move format."));
                    }
                }
            }
        }
        return reply.toString();
    }

    private boolean success(String authToken){
        return authToken != null;
    }
}
