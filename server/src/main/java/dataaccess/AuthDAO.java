package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public interface AuthDAO {
    void createAuth(AuthData authData) throws SQLException, DataAccessException;

    void clear() throws SQLException, DataAccessException;

    void deleteAuth(String authToken) throws SQLException, DataAccessException;
    boolean getAuthToken(String authToken) throws SQLException;
    AuthData getAuthToken1(String authToken) throws DataAccessException;
    }