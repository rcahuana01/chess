package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private final List<GameData> gameDataList;
    private int nextGameId;

    public SQLGameDAO() {
        this.gameDataList = new ArrayList<>();
        this.nextGameId = 1;
    }

    @Override
    public int createGame(String newGame) throws ResponseException {
        if (newGame == null) {
            throw new ResponseException(500, "Error: Bad request");
        }

        ChessGame chessGame = new ChessGame();
        String gameJson = new Gson().toJson(chessGame);
        GameData gameData = new GameData(nextGameId++, null, null, newGame, chessGame);
        gameDataList.add(gameData);

        return gameData.gameID();
    }

    @Override
    public GameData getGame(int gameId) throws ResponseException {
        for (GameData game : gameDataList) {
            if (game.gameID() == gameId) {
                return game;
            }
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws ResponseException {
        return new ArrayList<>(gameDataList);
    }


    @Override
    public void updateGame(GameData game) throws ResponseException {
        if (game == null) {
            throw new ResponseException(500, "Error: Game data cannot be null");
        }

        for (int i = 0; i < gameDataList.size(); i++) {
            if (gameDataList.get(i).gameID() == game.gameID()) {
                gameDataList.set(i, game);
                return;
            }
        }
        throw new ResponseException(404, "Error: Game not found");
    }

    @Override
    public void clear() throws ResponseException {
        gameDataList.clear();
    }
}
