package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData);

    void clear();

    void deleteAuth(String authToken);
    boolean getAuthToken(String authToken);
    AuthData getAuthToken1(String authToken);
    }