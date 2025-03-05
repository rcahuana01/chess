package dataaccess;

import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface GameDAO {
    void clear();
    void createGame(GameData gameData);
    GameData getGame(int gameId);

    void updateGameList(GameData gameData);

    Collection<GameData> getAvailableGames();
}
