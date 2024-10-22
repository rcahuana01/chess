package server;

import com.google.gson.Gson;
import dataaccess.ResponseException;
import model.GameData;
import model.JoinGameData;
import service.GameService;
import spark.*;

import java.util.Collection;
import java.util.Map;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object listGames(Request req, Response res) {
        try {
            Collection<GameData> games = gameService.listGames(req.headers("Authorization"));
            res.status(200);
            return new Gson().toJson(Map.of("games", games));
        } catch (ResponseException e){
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }

    public Object createGame(Request req, Response res) {
        try {
            GameData game = new Gson().fromJson(req.body(), GameData.class);
            res.status(200);
            return new Gson().toJson(Map.of("gameID", gameService.createGame(req.headers("Authorization"), game)));
        } catch (ResponseException e){
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }

    public Object joinGame(Request req, Response res) {
        try {
            JoinGameData joinGameData = new Gson().fromJson(req.body(), JoinGameData.class);
            gameService.joinGame(req.headers("Authorization"), joinGameData.playerColor(), joinGameData.gameID());
            res.status(200);
            return "";
        }catch (ResponseException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }
}