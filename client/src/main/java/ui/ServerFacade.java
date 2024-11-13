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
import java.util.Map;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

    public AuthData register(UserData user) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(serverURL + "/user")).POST(HttpRequest.
                BodyPublishers.ofString(new Gson().toJson(user))).header("Content-Type", "application/json").build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200) {
            return new Gson().fromJson(httpResponse.body(), AuthData.class);
        } else {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }

    public AuthData login(UserData user) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(serverURL + "/session")).POST(HttpRequest.
                BodyPublishers.ofString(new Gson().toJson(user))).header("Content-Type", "application/json").build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200) {
            return new Gson().fromJson(httpResponse.body(), AuthData.class);
        } else {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }

    public void logout(String authToken) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(serverURL + "/session")).DELETE().
                header("Authorization", authToken).build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (httpResponse.statusCode() != 200) {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }

    public ListGameData listGames(String authToken) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(serverURL + "/game")).
                GET().header("Authorization", authToken).build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200) {
            return new Gson().fromJson(httpResponse.body(), ListGameData.class);
        } else {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }

    public GameData createGame(String authToken, GameData game) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(serverURL + "/game")).POST(HttpRequest. BodyPublishers.ofString(new
                Gson().toJson(game))).header("Content-Type", "application/json").header("Authorization", authToken).build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200) {
            return new Gson().fromJson(httpResponse.body(), GameData.class);
        } else {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }

    public void joinGame(String authToken, int gameId, String playerColor) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(serverURL + "/game")).PUT(HttpRequest.
                BodyPublishers.ofString(new Gson().toJson(Map.of("playerColor", playerColor, "gameID",
                        gameId)))).header("Content-Type", "application/json").header("Authorization", authToken).build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200) {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }

    public void clear() throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(serverURL + "/db")).DELETE().build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200) {
            throw new Exception("Error: " + httpResponse.statusCode() + " " + httpResponse.body());
        }
    }
}
