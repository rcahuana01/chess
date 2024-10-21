package service;

import dataaccess.*;

public class DataService {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public DataService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    public void clear() throws ResponseException {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }
}
