package dataaccess;

import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface GameDAO {
    void clear() throws SQLException, DataAccessException;
    void createGame(GameData gameData) throws SQLException, DataAccessException;
    GameData getGame(int gameId) throws DataAccessException;

    void updateGameList(GameData gameData) throws SQLException, DataAccessException;

    Collection<GameData> getAvailableGames() throws DataAccessException;
}
