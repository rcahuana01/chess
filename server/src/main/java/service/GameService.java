package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;

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

    public GameData createGame(String gameName) throws DataAccessException {
//        if (!authDAO.isValidToken(authDAO.getAuthToken())){
//            throw new DataAccessException("Error: unauthorized");
//        }
        if (gameName == null) {
            throw new DataAccessException("Error: bad request");
        }

        GameData gameData = new GameData(1, null, null, gameName, null);
        gameDAO.createGame(gameData.gameID());
        return gameData;

    }

    public GameData joinGame(GameData gameData, int gameId) throws DataAccessException {
//        if (!authDAO.isValidToken(authDAO.getAuthToken())){
//            throw new DataAccessException("Error: unauthorized");
//        }
        GameData checkGame = gameDAO.getGame(gameId);
        if (checkGame==null){
            throw new DataAccessException("Error: bad request");
        }
        if (checkGame.whiteUsername()!=null&&checkGame.blackUsername()!=null){
            throw new DataAccessException("Error: already taken");
        }
        if (gameDAO.canJoinGame(gameData, gameId)){
            if (checkGame.whiteUsername()==null) {
                gameDAO.updateGamePlayers(gameId,userDAO.getUser(gameData.whiteUsername()));
            } else {
                gameDAO.updateGamePlayers(gameId,userDAO.getUser(gameData.blackUsername()));
            }
        }
        gameDAO.updateGameList(checkGame);
        return checkGame;
    }


    public List<GameData> listGames(String authToken) throws DataAccessException{
//        if (!authDAO.isValidToken(authToken)) {
//            throw new DataAccessException("Error: unauthorized");
//        }
        List<GameData> gameList = gameDAO.getAvailableGames();
        if (gameList==null){
            throw new DataAccessException("Error: bad request");
        }
        return gameList;

    }

}
