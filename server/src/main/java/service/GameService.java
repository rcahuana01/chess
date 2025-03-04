package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;

import java.util.UUID;

public class GameService {
    public UserDAO userDAO;
    public AuthDAO authDAO;
    public GameDAO gameDAO;
    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public GameData createGame(String gameName) throws DataAccessException {
        if (!authDAO.isValidToken(authDAO.getAuthToken())){
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameName == null) {
            throw new DataAccessException("Error: bad request");
        }

        GameData gameData = new GameData(1, null, null, gameName, null);
        gameDAO.createGame(gameData.gameID());
        return gameData;

    }

    public GameData joinGame(GameData gameData, int gameId) throws DataAccessException {
        if (!authDAO.isValidToken(authDAO.getAuthToken())){
            throw new DataAccessException("Error: unauthorized");
        }
        GameData checkGame = gameDAO.getGame(gameId);
        if (checkGame==null){
            throw new DataAccessException("Error: bad request");
        }
        if (gameDAO.canJoinGame(gameData, gameData.gameID())){
            gameDAO.updateGamePlayers(gameId, userDAO.getUser(gameData.blackUsername()));
        }
        gameDAO.updateGameList(gameData);
        return gameData;

    }

    public String getUsernameFromToken(String authHeader) {
        return null;
    }
}
