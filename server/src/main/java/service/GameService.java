package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.Objects;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO , AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }
    public Collection<GameData> listGames(String authToken) throws ResponseException {
        if (authToken == null || authToken.isEmpty() || authDAO.getAuth(authToken) == null ) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        try{
            return gameDAO.listGames();
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    public Integer createGame(String authToken, GameData game) throws ResponseException {
        if (authToken == null || authToken.isEmpty() || authDAO.getAuth(authToken) == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        if (game == null || game.gameName() == null || game.gameName().isEmpty()){
            throw new ResponseException(400, "Error: bad request");
        }
        try {
            return gameDAO.createGame(game);
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    public void joinGame(String authToken, String playerColor, int gameId) throws ResponseException {
        AuthData auth = authDAO.getAuth(authToken);
        GameData game = gameDAO.getGame(gameId);

        if (authToken == null || authToken.isEmpty() || auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        if (playerColor == null || gameId < -1 || game == null){
            throw new ResponseException(400, "Error: bad request");
        }
        if (!playerColor.isEmpty()) {
            if ((playerColor.equals("WHITE")) && (game.whiteUsername() != null)){
                throw new ResponseException(403, "Error: already taken");
            }
            if((playerColor.equals("BLACK")) && (game.blackUsername() != null)){
                throw new ResponseException(403, "Error: already taken");
            }
            try {
                if (playerColor.equals("WHITE")) {
                    gameDAO.updateGame(new GameData(gameId, auth.username(), game.blackUsername(), game.gameName(), game.game()));
                } else if (playerColor.equals("BLACK")) {
                    gameDAO.updateGame(new GameData(gameId, game.whiteUsername(), auth.username(), game.gameName(), game.game()));
                } else if (playerColor.equals("observer")) {
                    return;
                }
            } catch (Exception e) {
                throw new ResponseException(500, "Error: " + e.getMessage());
            }
        }

    }

}