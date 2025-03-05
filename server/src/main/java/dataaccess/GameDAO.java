package dataaccess;

import model.GameData;
import model.UserData;

import java.util.List;

public interface GameDAO {
    void clear();
    void createGame(int gameId);
    GameData getGame(int gameId);

    void updateGameList(GameData gameData);

    List<GameData> getAvailableGames();

    void updateGamePlayers(int i, UserData whitePlayer, boolean b);
}
