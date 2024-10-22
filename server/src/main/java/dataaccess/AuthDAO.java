package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String username) throws ResponseException;

    AuthData getAuth(String authToken) throws ResponseException;

    void deleteAuth(String authToken) throws ResponseException;

    void clear() throws ResponseException;
}