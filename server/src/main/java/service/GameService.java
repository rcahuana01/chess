package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.JoinRequest;

import java.sql.SQLException;
import java.util.*;

public class GameService {
    public UserDAO userDAO;
    public AuthDAO authDAO;
    public GameDAO gameDAO;
    public JoinRequest joinRequest;
    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.joinRequest = new JoinRequest(0, "WHITE");
    }

    public GameData createGame(String gameName, String authToken) throws DataAccessException, SQLException {
        if (authDAO.getAuthToken(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameName == null) {
            throw new DataAccessException("Error: bad request");
        }
        int gameId = new Random().nextInt(1000000);

        GameData gameData = new GameData(gameId, null, null, gameName, new ChessGame());
        gameDAO.createGame(gameData);
        return gameData;

    }

    public GameData joinGame(int gameId, String playerColor, String authToken) throws DataAccessException, SQLException {
        AuthData auth = authDAO.getAuthToken(authToken);
        if (auth== null){
            throw new DataAccessException("Error: unauthorized");
        }

        GameData checkGame = gameDAO.getGame(gameId);
        if (checkGame == null || playerColor == null) {
            throw new DataAccessException("Error: bad request");
        }

        if (playerColor.equals("WHITE") && checkGame.whiteUsername() != null) {
            throw new DataAccessException("Error: already taken");
        } else if (playerColor.equals("BLACK") && checkGame.blackUsername() != null) {
            throw new DataAccessException("Error: already taken");
        }

        if (playerColor.equals("WHITE")) {
            checkGame = new GameData(gameId, auth.username(), checkGame.blackUsername(), checkGame.gameName(), checkGame.game());
        } else if (playerColor.equals("BLACK")) {
            checkGame = new GameData(gameId, checkGame.whiteUsername(), auth.username(), checkGame.gameName(), checkGame.game());
        }else {
            throw new DataAccessException("Error: bad request");
        }
        gameDAO.updateGameList(checkGame);



        return checkGame;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException, SQLException {
        if (authDAO.getAuthToken(authToken)==null||authToken==null) {
                throw new DataAccessException("Error: unauthorized");
        }
        return gameDAO.getAvailableGames();

    }

    public void clear() throws DataAccessException, SQLException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }

}
