package dataaccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        games.put(gameId, new GameData(gameId, user.username(), null, null, null));
    }


    public void updateGameList(GameData gameData) {
        if (gameData != null){
            games.put(gameData.gameID(), gameData);
        }

    }

    public boolean canJoinGame(GameData gameData, int userId) {
        return gameData.whiteUsername() == null || gameData.blackUsername() == null;
    }

    public List<GameData> getAvailableGames(){
        List<GameData> availableGames = new ArrayList<>();
        for (GameData game : games.values()){
            if (game.whiteUsername() == null || game.blackUsername() == null) {
                availableGames.add(game);
            }
        }
        return availableGames;
    }

}
