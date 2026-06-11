package client;

import exceptions.ResponseException;
import resultsandrequests.RegisterUserRequest;
import resultsandrequests.RegisterUserResult;
import resultsandrequests.UIResponse;

import static client.ClientMain.State.*;

public class ClientUI {
    ServerFacade serverFacade;

    public ClientUI(ServerFacade serverFacade){
        this.serverFacade = serverFacade;
    }


    public String helpMenu(ClientMain.State state){
        StringBuilder uiOutput = new StringBuilder();
        if(state == SIGNED_OUT){
            uiOutput.append("REGISTER <Username> <Password> <Email> : To create an account\n");
            uiOutput.append("LOGIN <Username> <Password> : To log in to an existing account\n");
            uiOutput.append("QUIT : Exit Program\n");
        } else if(state == SIGNED_IN){
            uiOutput.append("CREATE <name> : Create a new chess game with a name\n");
            uiOutput.append("LIST : See a list of all chess games\n");
            uiOutput.append("JOIN <ID> <COLOR> : Join a game as BLACK or WHITE\n");
            uiOutput.append("OBSERVE <ID> : Choose a game to watch\n");
            uiOutput.append("LOGOUT : Sign out of account\n");
        } else if(state == IN_GAME){
            //TO DO
        }
        uiOutput.append("HELP : See List of Commands\n");
        return uiOutput.toString();
    }

    public UIResponse register(String username, String password, String email){
        RegisterUserRequest request = new RegisterUserRequest(username, password, email);
        try {
            RegisterUserResult result = serverFacade.register(request);
            return new UIResponse("Account successfully made and signed in as " + result.username(), result.authToken());
        } catch (ResponseException e){
            if(e.code() == ResponseException.Code.ServerError) {
                return new UIResponse("Error: Could not connect to the server.", null);
            }
            return new UIResponse(e.getMessage(), null);
        }
    }
}
