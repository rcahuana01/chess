package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;
import static dataaccess.DatabaseManager.configureDatabase;
public class SQLAuthDAO implements AuthDAO {
//    private final HashMap<String, AuthData> authDatabase = new HashMap<>();
    private static final String[] CREATE_TABLE_STMT = {
        """
        CREATE TABLE IF NOT EXISTS authDATA( 
        'authToken' varchar(255) NOT NULL,
        'username' varchar(255) NOT NULL,
        PRIMARY KEY ('authToken')
        )"""
};
    public SQLAuthDAO() {
        try {
            configureDatabase(CREATE_TABLE_STMT);
        } catch (DataAccessException e) {
            throw new RuntimeException("Unable to create authentication table", e);
        }
    }

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
