package dataaccess;

import model.GameData;
import model.UserData;

import java.util.List;

public interface GameDAO {
    void clear();
    void createGame(int gameId);
    GameData getGame(int gameId);

    void updateGameList(GameData gameData);
    boolean canJoinGame(GameData gameData, int userId);

    void updateGamePlayers(int gameId, UserData user);
    List<GameData> getAvailableGames();

}
