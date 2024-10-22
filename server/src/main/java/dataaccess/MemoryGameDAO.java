package dataaccess;

import dataaccess.*;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public int createGame(GameData newGame) throws ResponseException {
        int gameId = games.size()+1;
        GameData existingGame = new GameData(gameId, newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), newGame.game());
        games.put(gameId, existingGame);
        return gameId;
    }

    @Override
    public GameData getGame(int gameId) throws ResponseException {
        try {
            return games.get(gameId);
        }catch (Exception e){
            throw new ResponseException(400, "Error: Game not found");
        }
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void updateGame(GameData game) throws ResponseException {
        if (games.containsKey(game.gameID())) {
            games.put(game.gameID(), game);
        } else {
            throw new ResponseException(400, "Error: Game not found");
        }
    }

    @Override
    public void clear() throws ResponseException {
        try {
            games.clear();
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }
}