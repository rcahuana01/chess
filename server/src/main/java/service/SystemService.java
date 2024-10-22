package service;

import dataaccess.*;

public class SystemService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public SystemService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
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
