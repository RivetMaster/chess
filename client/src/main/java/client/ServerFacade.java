package client;

import com.google.gson.Gson;
import exceptions.*;
import model.*;
import resultsandrequests.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    //what client calls to, as if it was connecting to server, but here we take the request and turn it to HTTP,
    // and actually connect to the Server. Probably not where handle errors

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        HttpRequest req = buildRequest("DELETE", "/db", null, null);
        HttpResponse<String> response = sendRequest(req);
        handleResponse(response, null);
    }

    public RegisterUserResult register(RegisterUserRequest request) throws ResponseException {
        HttpRequest req = buildRequest("POST", "/user", request, null);
        HttpResponse<String> response = sendRequest(req);
        return handleResponse(response, RegisterUserResult.class);
    }

    public LogInResult logIn(LogInRequest request) throws ResponseException {
        HttpRequest req = buildRequest("POST", "/session", request, null);
        HttpResponse<String> response = sendRequest(req);
        return handleResponse(response, LogInResult.class);
    }

    public void logOut(LogOutRequest request) throws ResponseException {
        HttpRequest req = buildRequest("DELETE", "/session", null, request.authToken());
        HttpResponse<String> response = sendRequest(req);
        handleResponse(response, null);
    }

    public ListGamesResult listGames(ListGamesRequest request) throws ResponseException {
        HttpRequest req = buildRequest("GET", "/game", null, request.authToken());
        HttpResponse<String> response = sendRequest(req);
        return handleResponse(response, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException {
        HttpRequest req = buildRequest("POST", "/game",
                new CreateGameRequest(request.gameName(), null),
                request.authToken());
        HttpResponse<String> response = sendRequest(req);
        return handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest request) throws ResponseException {
        HttpRequest req = buildRequest("PUT", "/game",
                new JoinGameRequest(request.gameID(), request.playerColor(), null),
                request.auth().authToken());
        HttpResponse<String> response = sendRequest(req);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if(authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body, status);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    public String getServerUrl(){
        return serverUrl;
    }

}
