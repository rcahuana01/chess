package dataaccess;

import dataaccess.*;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData newUser) throws ResponseException {
        if (newUser.username() == null || newUser.username().isEmpty()) {
            throw new ResponseException(400, "Error: Username is required");
        }
        if (users.containsKey(newUser.username())) {
            throw new ResponseException(403, "Error: username already exists");
        }
        try {
            users.put(newUser.username(), newUser);
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }

    }

    @Override
    public UserData getUser(String username) throws ResponseException {
        try {
            return users.get(username);
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws ResponseException {
        try {
            users.clear();
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }
}