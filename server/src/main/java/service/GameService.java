package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;
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

    public GameData createGame(String gameName, String authToken) throws DataAccessException {
        if (!authDAO.getAuthToken(authToken)){
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameName == null) {
            throw new DataAccessException("Error: bad request");
        }

        GameData gameData = new GameData(1, null, null, gameName, null);
        gameDAO.createGame(gameData.gameID());
        return gameData;

    }

    public GameData joinGame(GameData gameData, String authToken) throws DataAccessException {
        GameData checkGame = gameDAO.getGame(gameData.gameID());
        if (!authDAO.getAuthToken(authToken)){
            throw new DataAccessException("Error: unauthorized");
        }
        if (checkGame==null){
            throw new DataAccessException("Error: bad request");
        }
        if (gameData.whiteUsername() != null) {
            // If the white player is not already assigned
            if (checkGame.whiteUsername() == null) {
                UserData whitePlayer = userDAO.getUser(gameData.whiteUsername());
                if (whitePlayer == null) {
                    throw new DataAccessException("Error: White player not found");
                }
                // Assign the white player to the game
                gameDAO.updateGamePlayers(gameData.gameID(), whitePlayer, true);
            } else {
                // If the white player is already assigned
                throw new DataAccessException("Error: White player already assigned");
            }
        }

        // Check and update for the black player
        if (gameData.blackUsername() != null) {
            // If the black player is not already assigned
            if (checkGame.blackUsername() == null) {
                UserData blackPlayer = userDAO.getUser(gameData.blackUsername());
                if (blackPlayer == null) {
                    throw new DataAccessException("Error: Black player not found");
                }
                // Assign the black player to the game
                gameDAO.updateGamePlayers(gameData.gameID(), blackPlayer, false);
            } else {
                // If the black player is already assigned
                throw new DataAccessException("Error: Black player already assigned");
            }
        }
        gameDAO.updateGameList(gameData);
        return gameData;
    }


    public List<GameData> listGames(String authToken) throws DataAccessException{
        if (!authDAO.getAuthToken(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
        return gameDAO.getAvailableGames();

    }

}
