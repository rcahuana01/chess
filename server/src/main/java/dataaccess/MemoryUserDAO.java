package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData userData){
        users.put(userData.username(), userData);
    }
    public UserData getUser(String username){

        return users.get(username);
    }

    public void clear(){
        users.clear();
    }

}
