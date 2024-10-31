package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.executeUpdate;

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
            String insertStatement = "INSERT INTO (whiteUsername, blackUsername, gameName, name) VALUES (?, ?, ?, ?)";
            return DatabaseManager.executeUpdate(insertStatement, null, null, "game", gameJSON);
        } catch (DataAccessException e){
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameId) throws ResponseException {
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement("SELECT * FROM gameData WHERE gameId = ?")){
            stmt.setInt(1, gameId);
            var rs = stmt.executeQuery();
            if (rs.next()){
                ChessGame newGame = new ChessGame();
                return new GameData(gameId, null, null, "game", newGame);
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
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement("SELECT * FROM gameData")){
            var rs = stmt.executeQuery();
            while (rs.next()){
                ChessGame chessGame = new Gson().fromJson(rs.getString("game"), ChessGame.class);                games.add(new GameData(rs.getInt("gameId"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), chessGame));
            }
            return games;
        } catch (DataAccessException | SQLException e){
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData game) throws ResponseException {
        try {
            String gameJson = new Gson().toJson(game.game());
            DatabaseManager.executeUpdate("UPDATE gameData SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameId = ?",
                    null, null, "game", gameJson, "game132");
        } catch (DataAccessException e){
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws ResponseException {
        try {
            var conn = DatabaseManager.executeUpdate("DELETE gameData ALL");
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

}