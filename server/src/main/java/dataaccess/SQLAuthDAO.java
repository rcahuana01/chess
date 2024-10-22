package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> authDatabase = new HashMap<>();

    public SQLAuthDAO() {}

    @Override
    public AuthData createAuth(String username) throws ResponseException {
        if (username == null || username.isEmpty()) {
            throw new ResponseException(500, "Error: Username cannot be empty");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authDatabase.put(authToken, authData);
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException {
        if (authToken == null || authToken.isEmpty()) {
            throw new ResponseException(400, "Error: Invalid auth token");
        }

        // Simulate retrieving AuthData from the mock database
        return authDatabase.get(authToken); // return null if not found
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {
        if (authToken == null || authToken.isEmpty()) {
            return; // Don't throw exception on invalid token
        }

        // Simulate deleting the AuthData from the mock database
        authDatabase.remove(authToken); // remove without throwing an exception if token not found
    }

    @Override
    public void clear() throws ResponseException {
        authDatabase.clear();
    }
}
