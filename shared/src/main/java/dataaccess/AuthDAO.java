package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public interface AuthDAO {
    void createAuth(AuthData authData) throws SQLException, DataAccessException;
    void clear() throws SQLException, DataAccessException;
    void deleteAuth(String authToken) throws SQLException, DataAccessException;
    AuthData getAuthToken(String authToken) throws DataAccessException;
    }