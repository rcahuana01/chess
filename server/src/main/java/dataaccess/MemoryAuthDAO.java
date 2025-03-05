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
    public boolean getAuthToken(String authToken) {
        return auths.containsKey(authToken);
    }
    public AuthData getAuthToken1(String authToken) {
        return auths.get(authToken);
    }
}
