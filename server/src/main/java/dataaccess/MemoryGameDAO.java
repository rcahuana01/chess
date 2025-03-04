package dataaccess;

import model.GameData;
import model.UserData;

import java.util.HashMap;

public class MemoryGameDAO implements  GameDAO{
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public void clear(){
        games.clear();
    }

    public void createGame(int gameId){
        games.put(gameId, new GameData(gameId, null, null, null, null));
    }

    public GameData getGame(int gameId){
        games.get(gameId);
        return null;
    }

    public void updateGamePlayers(int gameId, UserData user) {

    }

    public void updateGameList() {

    }

    public boolean canJoinGame(GameData gameData, int userId) {
        return false;
    }

}
