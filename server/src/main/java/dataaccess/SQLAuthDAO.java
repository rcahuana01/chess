package dataaccess;


import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

import static dataaccess.DatabaseManager.configureDatabase;

public class SQLAuthDAO implements AuthDAO {
    private static final String[] CREATE_TABLE_STMT = {
            """
            CREATE TABLE IF NOT EXISTS  authData (
              `authToken` varchar(255) NOT NULL,
              `username` varchar(255) NOT NULL,
              PRIMARY KEY (`authToken`)
            )"""
    };

    public SQLAuthDAO() {
        try {
            configureDatabase(CREATE_TABLE_STMT);
        } catch (DataAccessException e) {
            throw new RuntimeException("Unable to create auth's table", e);
        }
    }

    @Override
    public AuthData createAuth(String username) throws ResponseException {
        try {
            String insertStatement = "INSERT INTO authData (username, authToken) VALUES (?, ?)";
            AuthData authData = new AuthData(UUID.randomUUID().toString(), username);
            DatabaseManager.executeUpdate(insertStatement, authData.username(), authData.authToken());
            return authData;
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }

    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException {
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement("SELECT * FROM authData WHERE authToken = ?")) {
            stmt.setString(1, authToken);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new AuthData(rs.getString("authToken"), rs.getString("username"));
            } else {
                return null;
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement("DELETE FROM authData WHERE authToken = ?")) {
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws ResponseException {
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement("DELETE FROM authData")) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }
}