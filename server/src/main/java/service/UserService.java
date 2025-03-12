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
            userDAO.createUser(user);
            storeUserPassword(user.username(), user.password());
            AuthData authData = new AuthData(UUID.randomUUID().toString(), user.username());
            authDAO.createAuth(authData);

            return authData;
        } else {
            throw new DataAccessException("Error: already taken");
        }

    }

    void storeUserPassword(String username, String clearTextPassword) throws SQLException, DataAccessException {
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

        // write the hashed password in database along with the user's other information
        userDAO.writeHashedPasswordToDatabase(username, hashedPassword);
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

    boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        // read the previously hashed password from the database
        var hashedPassword = userDAO.readHashedPasswordFromDatabase(username);

        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    public AuthData logout(String authToken) throws DataAccessException, SQLException {
        if (!authDAO.getAuthToken(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
        authDAO.deleteAuth(authToken);
        return null;
    }

}
