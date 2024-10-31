package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.configureDatabase;

public class SQLUserDAO implements UserDAO {
    private static final String[] CREATE_TABLE_STMT = {
            """
            CREATE TABLE IF NOT EXISTS userData (
			`username` varchar(255) NOT NULL,
            `password` varchar(255) NOT NULL,
            `email` varchar(255) NOT NULL,
            PRIMARY KEY (`username`)
            )"""
    };

    public SQLUserDAO() {
        try {
            configureDatabase(CREATE_TABLE_STMT);
        } catch (DataAccessException e) {
            throw new RuntimeException("Unable to create user's table", e);
        }
    }

    @Override
    public void createUser(UserData newUser) throws ResponseException {
        try {
            String insertStatement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
            DatabaseManager.executeUpdate(insertStatement, newUser.username(), encryptPassword(newUser.password()), newUser.email());
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws ResponseException {
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement("SELECT username, password, email FROM userData WHERE username = ?")) {
            stmt.setString(1, username);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            } else {
                return null;
            }
        } catch (DataAccessException | SQLException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws ResponseException {
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement("DELETE FROM userData")) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    private String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}