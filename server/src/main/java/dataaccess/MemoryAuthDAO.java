package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> auths = new HashMap<>();

    public void createAuth(AuthData authData){
        auths.put(authData.authToken(), authData);
    }
    void getAuth(){

    }
    public void clear(){
        auths.clear();
    }
}
