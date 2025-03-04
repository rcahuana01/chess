package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> auths = new HashMap<>();

    public void createAuth(AuthData authData){
        auths.put(authData.authToken(), authData);
    }
    public void deleteAuth(String authToken){
        auths.remove(authToken);
    }
    public void clear(){
        auths.clear();
    }
    public boolean isValidToken(String authToken) {
        return auths.containsKey(authToken);
    }
    public String getAuthToken(){
        return auths.keySet().iterator().next();
    }
}
