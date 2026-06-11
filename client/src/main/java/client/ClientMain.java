package client;

import chess.*;
import resultsandrequests.UIResponse;

import java.util.Scanner;

import static client.ClientMain.State.*;

public class ClientMain {
    private static ServerFacade server;
    private State state;
    private static ClientUI ui;
    private String authToken;

    public enum State {
        SIGNED_OUT,
        SIGNED_IN,
        IN_GAME
    }

    public static void main(String[] args) {
        System.out.println("♕ Welcome to the 240 Chess Client. Type Help for help getting started. ♕" );
        var url = "http://localhost:" + 8080;
        server = new ServerFacade(url);
        ui = new ClientUI(server);

        ClientMain client = new ClientMain();
        client.scanInput();
    }

    private void scanInput(){
        Scanner scanner = new Scanner(System.in);
        state = SIGNED_OUT;
        //loop with scanner object
        while(true){
            System.out.print(">>>>");
            String line = scanner.nextLine();
            //should be able to quit from anywhere in the program? Have so if logged in and quit logs out first?
            if(state == SIGNED_OUT &&
                    (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("q"))){
                break;
            }
            if(line.equalsIgnoreCase("help") || line.equalsIgnoreCase("h")){
                System.out.println(ui.helpMenu(state));
            }
            else if(state == SIGNED_OUT){
                System.out.println(signedOutREPL(line));
            }
        }
        //can always access help, so check if is help first
        //if SIGNED_OUT send to signed out repl
        //if signed_in send to signed in repl
        //if in-game send to game repl

    }

    private String signedOutREPL(String line){
        String[] words = line.split(" ");
        StringBuilder reply = new StringBuilder();
        String command = "";

        if(words.length == 0){
            reply.append("Type HELP to see a list of valid commands.");
        } else{
            command = words[0];
        }

        if(command.equalsIgnoreCase("register")){
            if(words.length != 4){
                reply.append("Expecting register <USERNAME> <PASSWORD> <EMAIL>");
            } else{
                UIResponse response = ui.register(words[1], words[2], words[3]);
                reply.append(response.message());
                if(response.authToken() != null) {
                    authToken = response.authToken();
                    state = SIGNED_IN;
                }
            }
        } else if(command.equalsIgnoreCase("login")){
            reply.append("Trying to login...");
        } else if(reply.isEmpty()){
            reply.append("Type HELP to see a list of valid commands.");
        }
        return reply.toString();
    }
}
