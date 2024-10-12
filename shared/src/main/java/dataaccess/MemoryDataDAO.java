package dataaccess;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import model.UserData;
import chess.ChessGame;
import model.AuthData;

public class InMemoryDataAccess implements DataAccess{
    private Map<String, UserData> users = new HashMap<>();
    private Map<String, ChessGame> games = new HashMap<>();
    private Map<String, AuthData> authToken = new HashMap<>();
    public void clear(){
        users.clear();
        games.clear();
        authToken.clear();
    }

    public void createUser(UserData user){
        users.put(user.getUsername(), user);
    }

    public UserData getUser(String username){
        return users.get(username);
    }
}
