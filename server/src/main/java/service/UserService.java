package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws ResponseException {
        if (user == null || user.username() == null || user.username().isEmpty() || user.password() == null || user.password().isEmpty() || user.email() == null || user.email().isEmpty()) {
            throw new ResponseException(400, "Error: Bad Request");
        }

        UserData currentUser = userDAO.getUser(user.username());
        if (currentUser != null) {
            throw new ResponseException(403, "Error: already taken");
        }

        // Store the password in plain text (no hashing)
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

        // Retrieve the user from the database
        UserData currentUser = userDAO.getUser(user.username());

        // Debugging: Check if user exists
        if (currentUser == null) {
            System.out.println("User not found for username: " + user.username());
            throw new ResponseException(401, "Error: unauthorized - User not found");
        }

        // Debugging: Check if password matches
        if (!user.password().equals(currentUser.password())) {
            System.out.println("Passwords do not match!");
            System.out.println("Entered password: " + user.password());
            System.out.println("Stored password: " + currentUser.password());
            throw new ResponseException(401, "Error: unauthorized - Invalid password");
        }

        // If successful, create and return an auth token
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
