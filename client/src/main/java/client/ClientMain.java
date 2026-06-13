package client;

import chess.*;
import resultsandrequests.UIResponse;
import static ui.EscapeSequences.*;


import java.util.Scanner;
import java.util.regex.Pattern;

import static client.ClientMain.State.*;

public class ClientMain {
    private State state;
    private static ClientUI ui;
    private String authToken;

    public enum State {
        SIGNED_OUT,
        SIGNED_IN,
        PLAYING_GAME,
        WATCHING_GAME
    }

    public static void main(String[] args) {
        System.out.println("♕ Welcome to the 240 Chess Client. Type "+ SET_TEXT_BOLD+ "HELP"
            + RESET_TEXT_BOLD_FAINT + " for help getting started. ♕" );

        var url = "http://localhost:" + 8080;
        ServerFacade server = new ServerFacade(url);
        ui = new ClientUI(server);

        ClientMain client = new ClientMain();
        client.scanInput();
    }

    private void scanInput(){
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
                output.append(ui.helpMenu(state));
            }
            //clear (TESTING only)
            else if(testing && line.equalsIgnoreCase("clear")){
                output.append(ui.clear());
                state = SIGNED_OUT;
            }
            //Signed out options
            else if(state == SIGNED_OUT){
                output.append(signedOutREPL(words));
            }
            //signed in options
            else if(state == SIGNED_IN){
                output.append(signedInREPL(words));
            }
            //in game options
            else if(state == WATCHING_GAME || state == PLAYING_GAME){
                output.append(inGameREPL(words));
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
        String command  = words[0];

        //REGISTER
        if(command.equalsIgnoreCase("register")){
            if(words.length != 4){
                reply.append("Expecting ").append(ClientUI.bold("register <USERNAME> <PASSWORD> <EMAIL>"));
            } else{
                UIResponse response = ui.register(words[1], words[2], words[3]);
                reply.append(response.message());
                if(response.authToken() != null) {
                    authToken = response.authToken();
                    state = SIGNED_IN;
                }
            }
        }
        //LOGIN
        else if(command.equalsIgnoreCase("login")){
            if(words.length != 3){
                reply.append("Expecting ").append(ClientUI.bold("login <USERNAME> <PASSWORD>"));
            } else{
                UIResponse response = ui.login(words[1], words[2]);
                reply.append(response.message());
                if(response.authToken() != null) {
                    authToken = response.authToken();
                    state = SIGNED_IN;
                }
            }
        }
        return reply.toString();
    }

    private String signedInREPL(String[] words){
        StringBuilder reply = new StringBuilder();
        String command = words[0];

        //LOGOUT
        if(command.equalsIgnoreCase("logout")){
            if(words.length != 1){
                reply.append("Expecting ").append(ClientUI.bold("logout"));
            } else{
                UIResponse response = ui.logout(authToken);
                reply.append(response.message());
                if(response.authToken() == null) {
                    authToken = null;
                    state = SIGNED_OUT;
                }
            }
        }
        //CREATE GAME
        else if(command.equalsIgnoreCase("create")){
            if(words.length != 2){
                reply.append("Expecting ").append(ClientUI.bold("create <NAME>"));
            } else{
                String response = ui.create(words[1], authToken);
                reply.append(response);
            }
        }
        //LIST GAMES
        else if(command.equalsIgnoreCase("list")){
            if(words.length != 1){
                reply.append("Expecting ").append(ClientUI.bold("list"));
            } else{
                String response = ui.list(authToken);
                reply.append(response);
            }
        }
        // JOIN GAME
        else if(command.equalsIgnoreCase("join")){
            if(words.length != 3){
                reply.append("Expecting ").append(ClientUI.bold( "join <ID> <COLOR>"));
            } else{
                try{
                    int id = Integer.parseInt(words[1]);
                    ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(words[2].toUpperCase());
                    UIResponse response = ui.join(id, color, authToken);
                    reply.append(response.message());
                    if(response.authToken() != null) {
                        authToken = response.authToken();
                        state = PLAYING_GAME;
                    }
                } catch (NumberFormatException e) {
                    reply.append(ClientUI.red("Error: Invalid Game ID"));
                } catch(IllegalArgumentException e){
                    reply.append(ClientUI.red("Error: Invalid color. Expecting " + ClientUI.bold("WHITE")
                            + " or " + ClientUI.bold("BLACK") + "."));
                }
            }
        }
        //OBSERVE GAME
        else if(command.equalsIgnoreCase("observe")){
            if(words.length != 2){
                reply.append("Expecting ").append(ClientUI.bold("observe <ID>"));
            } else{
                try{
                    int id = Integer.parseInt(words[1]);
                    UIResponse response = ui.observe(id, authToken);
                    reply.append(response.message());
                    if(response.authToken() != null) {
                        authToken = response.authToken();
                        state = WATCHING_GAME;
                    }
                } catch (NumberFormatException e) {
                    reply.append(ClientUI.red("Error: Invalid Game ID"));
                }
            }
        }
        return reply.toString();
    }

    private String inGameREPL(String[] words){
        StringBuilder reply = new StringBuilder();
        String command = words[0];

        //LEAVE
        if(command.equalsIgnoreCase("leave")){
            if(words.length != 1){
                reply.append("Expecting ").append(ClientUI.bold("leave"));
            }
            else{
                reply.append("Left game.");
                //Remove from game, allowing other people to join game
                state = SIGNED_IN;
            }
        }
        //REDRAW
        if(command.equalsIgnoreCase("redraw")){
            if(words.length != 1){
                reply.append("Expecting ").append(ClientUI.bold("redraw"));
            }
            else{
                //query server for the board, server should have list of what game they're in
            }
        }
        //MOVE
        if(state == PLAYING_GAME && command.equalsIgnoreCase("move")){
            if(words.length != 3){
                reply.append("Expecting ").append(ClientUI.bold("move <Starting Position> <Ending Position>"));
            }
            else{
                if(Pattern.matches("[a-h][1-8]", words[1]) &&
                    Pattern.matches("[a-h][1-8]", words[2])){
                    //call move maker, check if valid
                    //check moves to see if valid, starting a-h
                    //query server, print out new board, notification that move was made.
                } else{
                    reply.append(ClientUI.red("Error: Invalid move format."));
                }

            }
        }
        //RESIGN
        if(state == WATCHING_GAME && command.equalsIgnoreCase("resign")){
            if(words.length != 1){
                reply.append("Expecting ").append(ClientUI.bold("resign"));
            }
        }
        //HIGHLIGHT legal moves
        if(command.equalsIgnoreCase("highlight")){
            if(words.length != 2){
                reply.append("Expecting ").append(ClientUI.bold("highlight <Piece Position>"));
            }
            else{
                if(Pattern.matches("[a-h][1-8]", words[1])){

                } else{
                    reply.append(ClientUI.red("Error: Invalid move format."));
                }
            }
        }
        return reply.toString();
    }
}
