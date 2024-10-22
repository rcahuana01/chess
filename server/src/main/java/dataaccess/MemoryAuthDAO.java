package dataaccess;

import dataaccess.*;
import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private HashMap<String, AuthData> authTokens = new HashMap<>();

    @Override
    public AuthData createAuth(String username) throws ResponseException {
        if (username == null || username.isEmpty()) {
            throw new ResponseException(400, "Error: Empty username");
        }
        AuthData authData = new AuthData(UUID.randomUUID().toString(), username);
        authTokens.put(authData.authToken(), authData);
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException {
        try {
            return authTokens.get(authToken);
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {
        if (!authTokens.containsKey(authToken)) {
            throw new ResponseException(400, "Error: Auth token not found");
        }
        try {
            authTokens.remove(authToken);
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws ResponseException {
        try {
            authTokens.clear();
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }
}