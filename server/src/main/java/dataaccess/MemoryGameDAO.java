package dataaccess;

import model.GameData;
import model.JoinRequest;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements  GameDAO{
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public void clear(){
        games.clear();
    }

    public void createGame(GameData gameData){
        games.put(gameData.gameID(), gameData);
    }

    public GameData getGame(int gameId){
        return games.get(gameId);
    }

    public void updateGameList(GameData gameData) {
        if (games.containsKey(gameData.gameID())) {
            games.put(gameData.gameID(), gameData);
        }
    }

    public Collection<GameData> getAvailableGames() {
        return games.values();
    }
}
