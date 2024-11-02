package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.ListGameData;
import model.UserData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

    public AuthData register(UserData user) throws  Exception{
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(serverURL + "/user")).POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(user))).header("Content-Type", "application/json").build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200){
            return new Gson().fromJson(httpResponse.body(), AuthData.class);
        } else {
            throw new Exception("Error: " + httpResponse.body(), AuthData.class);
        }
    }

    public AuthData login(UserData user) throws Exception{
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(serverUrl + "/session")).POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(user))).header("Content-Type", "application/json").build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200){
            return new Gson().fromJson(httpResponse.body(), AuthData.class);
        } else {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }

    public void logout(String authToken) throws Exception{
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(serverUrl + "/session")).DELETE().header("Authorization", authToken).build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

    }

    public ListGameData listGames(String authToken) throws Exception{
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(serverUrl + "/game")).GET().header("Authorization", authToken).build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200){
            return new Gson().fromJson(httpResponse.body(), ListGameData.class);
        } else {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }

    public GameData createGame(String authToken, GameData game) throws Exception{
        HttpRequest httpRequest = HttpRequest.newBuilder().uri()
    }
}
