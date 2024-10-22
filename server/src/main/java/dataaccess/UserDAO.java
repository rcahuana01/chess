package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData newUser) throws ResponseException;

    UserData getUser(String username) throws ResponseException;

    void clear() throws ResponseException;

}