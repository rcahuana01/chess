package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private final List<GameData> gameDataList; // In-memory storage for game data
    private int nextGameId; // To simulate auto-incrementing IDs

    public SQLGameDAO() {
        this.gameDataList = new ArrayList<>();
        this.nextGameId = 1; // Start IDs from 1
    }

    @Override
    public int createGame(GameData newGame) throws ResponseException {
        if (newGame == null || newGame.whiteUsername() == null || newGame.gameName() == null) {
            throw new ResponseException(400, "Error: Bad request");
        }

        // Simulate storing the game and assigning an ID
        ChessGame chessGame = new ChessGame();
        String gameJson = new Gson().toJson(chessGame);
        GameData gameData = new GameData(nextGameId++, newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), chessGame);
        gameDataList.add(gameData);

        return gameData.gameID(); // Return the generated gameId
    }

    @Override
    public GameData getGame(int gameId) throws ResponseException {
        for (GameData game : gameDataList) {
            if (game.gameID() == gameId) {
                return game; // Return the game if found
            }
        }
        throw new ResponseException(404, "Error: Game not found"); // Game not found
    }

    @Override
    public Collection<GameData> listGames() throws ResponseException {
        return new ArrayList<>(gameDataList); // Return a copy of the list of games
    }

    @Override
    public void updateGame(GameData game) throws ResponseException {
        for (int i = 0; i < gameDataList.size(); i++) {
            if (gameDataList.get(i).gameID() == game.gameID()) {
                gameDataList.set(i, game); // Update the existing game data
                return;
            }
        }
        throw new ResponseException(404, "Error: Game not found"); // Game not found
    }

    @Override
    public void clear() throws ResponseException {
        gameDataList.clear(); // Clear the in-memory list
    }
}