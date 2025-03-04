package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData);

    void clear();

    void deleteAuth(String authToken);
    boolean isValidToken(String authToken);
    String getAuthToken();
    }