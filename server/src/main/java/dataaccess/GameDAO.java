package dataaccess;

import model.GameData;
import model.UserData;

public interface GameDAO {
    void clear();
    void createGame(int gameId);
    GameData getGame(int gameId);

    void updateGamePlayers(int gameId, UserData user);

    void updateGameList();
    boolean canJoinGame(GameData gameData, int userId);
}
