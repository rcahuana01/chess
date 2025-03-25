package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import model.JoinRequest;
import model.UserData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;

    }

    public void create(String... params) throws DataAccessException{

        int gameId = new Random().nextInt(1000000);
        GameData game = new GameData(gameId, null,null, params[0], new ChessGame());
        this.makeRequest("POST", "/game", game, GameData.class);
    }

    public Collection<GameData> list() throws DataAccessException{
        record ListGames(Collection<GameData> games){}
        var response = this.makeRequest("GET", "/game", null, ListGames.class);
        return response.games;
    }

    public void join(String... params) throws DataAccessException{
        var game = new JoinRequest(Integer.parseInt(params[0]), params[1]);
        this.makeRequest("PUT", "/game", game, JoinRequest.class);
    }

    public void observe(String... params) throws DataAccessException{
        var game = new JoinRequest(Integer.parseInt(params[0]), null);
        this.makeRequest("PUT", "/game", game, JoinRequest.class);
    }

    public void logout() throws DataAccessException{
        this.makeRequest("DELETE", "/session", null, null);
    }

    public void login(String... params) throws DataAccessException{
        UserData user = new UserData(params[0], params[1],null);
        this.makeRequest("POST", "/session", user, UserData.class);
    }

    public void register(String... params) throws DataAccessException{
        UserData user = new UserData(params[0], params[1], params[2]);
        this.makeRequest("POST", "/user", user, UserData.class);
    }

    public void quit() throws DataAccessException{
        this.makeRequest("DELETE", "/user", null, null);
    }


    public <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw DataAccessException.fromJson(respErr);
                }
            }

            throw new DataAccessException("other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
