package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static dataaccess.DatabaseManager.configureDatabase;

public class SQLGameDAO implements GameDAO {
    private static final String[] CREATE_TABLE_STMT = {
            """
            CREATE TABLE IF NOT EXISTS gameData (
            `gameId` int UNSIGNED NOT NULL AUTO_INCREMENT,
            `whiteUsername` varchar(255) NULL,
            `blackUsername` varchar(255) NULL,
            `gameName` varchar(255) NOT NULL,
            `game` TEXT NULL,
            PRIMARY KEY (`gameId`)
            )"""
    };

    public SQLGameDAO() {
        try {
            configureDatabase(CREATE_TABLE_STMT);
        } catch (DataAccessException e) {
            throw new RuntimeException("Unable to create game's table", e);
        }
    }

    @Override
    public int createGame(String newGame) throws ResponseException {
        try {
            ChessGame game = new ChessGame();
            String gameJSON = new Gson().toJson(game);
            String insertStatement = "INSERT INTO gameData(whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
            return DatabaseManager.executeUpdate(insertStatement, null, null, newGame, gameJSON);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameId) throws ResponseException {
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement("SELECT * FROM gameData WHERE gameId = ?")) {
            stmt.setInt(1, gameId);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                ChessGame newGame = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                return new GameData(rs.getInt("gameId"), rs.getString("whiteUsername"),
                        rs.getString("blackUsername"), rs.getString("gameName"), newGame);
            } else {
                return null;
            }
        } catch (DataAccessException | SQLException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames() throws ResponseException {
        Collection<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement("SELECT * FROM gameData")) {
            var rs = stmt.executeQuery();
            while (rs.next()) {
                String gameJson = rs.getString("game");
                ChessGame chessGame = gameJson != null ? new Gson().fromJson(gameJson, ChessGame.class) : null;
                games.add(new GameData(rs.getInt("gameId"), rs.getString("whiteUsername"),
                        rs.getString("blackUsername"), rs.getString("gameName"), chessGame));
            }
            return games;
        } catch (DataAccessException | SQLException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData game) throws ResponseException {
        try {
            String gameJson = new Gson().toJson(game.game());
            DatabaseManager.executeUpdate("UPDATE gameData SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameId = ?",
                    game.whiteUsername(), game.blackUsername(), game.gameName(), gameJson, game.gameID());
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws ResponseException {
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement("DELETE FROM gameData")) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

}