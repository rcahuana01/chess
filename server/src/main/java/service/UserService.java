package service;

import dataaccess.AuthDAO;
import dataaccess.ResponseException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws ResponseException {
        if (user == null || user.username() == null || user.username().isEmpty() || user.password() == null
                || user.password().isEmpty() || user.email() == null || user.email().isEmpty()) {
            throw new ResponseException(400, "Error: Bad Request");
        }

        UserData currentUser = userDAO.getUser(user.username());
        if (currentUser != null) {
            throw new ResponseException(403, "Error: already taken");
        }

        try {
            userDAO.createUser(user);
            return authDAO.createAuth(user.username());
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    public AuthData login(UserData user) throws ResponseException {
        if (user == null || user.username() == null || user.username().isEmpty() || user.password() == null || user.password().isEmpty()) {
            throw new ResponseException(401, "Error: unauthorized - Missing credentials");
        }

        UserData currentUser = userDAO.getUser(user.username());

        if ((currentUser == null) || !(BCrypt.checkpw(user.password(), currentUser.password()))) {
            throw new ResponseException(401, "Error: unauthorized - User not found");
        }


        try {
            return authDAO.createAuth(user.username());
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }


    public void logout(String authToken) throws ResponseException {
        if (authToken == null || authToken.isEmpty() || authDAO.getAuth(authToken) == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        try {
            authDAO.deleteAuth(authToken);
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }
}
