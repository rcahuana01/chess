package dataaccess;

import model.AuthData;

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
    public AuthData getAuthToken(String authToken) {
        return auths.get(authToken);
    }
}
