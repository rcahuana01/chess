package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class UserService {
    public static UserDAO userDAO;
    public static AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }


    public static AuthData register(UserData user) throws DataAccessException, SQLException {
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (userDAO.getUser(user.username()) == null) {
            userDAO.createUser(user);
            AuthData authData = new AuthData(UUID.randomUUID().toString(), user.username());
            authDAO.createAuth(authData);

            return authData;
        } else {
            throw new DataAccessException("Error: already taken");
        }

    }

    public AuthData login(UserData user) throws DataAccessException, SQLException {
        if (user.username() == null || user.password() == null) {
            throw new DataAccessException("Error: bad request");
        }
        UserData checkUser = userDAO.getUser(user.username());
        if (checkUser == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (Objects.equals(checkUser.username(), user.username()) && verifyUser()) {
            AuthData authData = new AuthData(UUID.randomUUID().toString(), user.username());
            authDAO.createAuth(authData);

            return authData;
        } else {
            throw new DataAccessException("Error: unauthorized");
        }

    }

    public AuthData logout(String authToken) throws DataAccessException, SQLException {
        if (!authDAO.getAuthToken(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
        authDAO.deleteAuth(authToken);
        return null;
    }

}
