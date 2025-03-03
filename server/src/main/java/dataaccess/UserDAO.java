package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData userData);
    UserData getUser(String username);
    void insertUser(String username);
    void clear();
}
