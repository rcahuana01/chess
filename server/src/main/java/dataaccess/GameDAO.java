package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String newGame) throws ResponseException;

    GameData getGame(int gameId) throws ResponseException;

    Collection<GameData> listGames() throws ResponseException;

    void updateGame(GameData game) throws ResponseException;

    void clear() throws ResponseException;
}