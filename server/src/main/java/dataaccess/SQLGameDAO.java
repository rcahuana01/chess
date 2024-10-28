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

public class SQLGameDAO implements GameDAO {
    private static final String[] CREATE_TABLE_STMT = {
            """
            CREATE TABLE IF NOT EXISTS gameData (
            'gameId' int UNSIGNED NOT NULL AUTO_INCREMENT,
            'whiteUsername' varchar(255) NULL,
            'blackUsername' varchar(255) NULL,
            'gameName' varchar(255) NOT NULL,
            'game' TEST NULL,
            PRIMARY KEY ('gameId')
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
        try {
            ChessGame game = new ChessGame();
            String
        };
    }

    @Override
    public Collection<GameData> listGames() throws ResponseException {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws ResponseException {

    }

    @Override
    public void clear() throws ResponseException {

    }

}