package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {
    // A mock database using a HashMap
    private final Map<String, AuthData> authDatabase = new HashMap<>();

    public SQLAuthDAO() {
    }

    @Override
    public AuthData createAuth(String username) throws ResponseException {
        if (username == null || username.isEmpty()) {
            throw new ResponseException(400, "Error: Username cannot be empty");
        }

        // Generate a new authToken using UUID
        String authToken = UUID.randomUUID().toString();

        // Create a new AuthData object
        AuthData authData = new AuthData(authToken, username);

        // Simulate storing the authData in the mock database
        authDatabase.put(authToken, authData);

        // Return the created AuthData object
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException {
        if (authToken == null || authToken.isEmpty()) {
            throw new ResponseException(400, "Error: Invalid auth token");
        }

        // Simulate retrieving AuthData from the mock database
        AuthData authData = authDatabase.get(authToken);

        if (authData == null) {
            throw new ResponseException(404, "Error: Auth token not found");
        }

        // Return the retrieved AuthData object
        return authData;
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {
        if (authToken == null || authToken.isEmpty()) {
            throw new ResponseException(400, "Error: Invalid auth token");
        }

        // Simulate deleting the AuthData from the mock database
        if (authDatabase.remove(authToken) == null) {
            throw new ResponseException(404, "Error: Auth token not found");
        }
    }

    @Override
    public void clear() throws ResponseException {
        // Simulate clearing all auth data from the mock database
        authDatabase.clear();
    }
}