package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

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


    public AuthData register(UserData user) throws DataAccessException, SQLException {
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (userDAO.getUser(user.username()) == null) {
            String hashPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            userDAO.createUser(new UserData(user.username(), hashPassword, user.email()));
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
        if (verifyUser(user.username(), user.password())) {
            AuthData authData = new AuthData(UUID.randomUUID().toString(), user.username());
            authDAO.createAuth(authData);

            return authData;
        } else {
            throw new DataAccessException("Error: unauthorized");
        }

    }

    boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException, SQLException {
        // read the previously hashed password from the database
        var hashedPassword = userDAO.getUser(username).password();
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    public AuthData logout(String authToken) throws DataAccessException, SQLException {
        if (authDAO.getAuthToken1(authToken)==null) {
            throw new DataAccessException("Error: unauthorized");
        }
        authDAO.deleteAuth(authToken);
        return null;
    }

}
