package dataaccess;

import dataaccess.*;
import model.GameData;
import java.util.Collection;

public interface GameDAO {
    int createGame(GameData newGame) throws ResponseException;
    GameData getGame(int gameId) throws ResponseException;
    Collection<GameData> listGames() throws ResponseException;
    void updateGame(GameData game) throws ResponseException;
    void clear() throws ResponseException;
}