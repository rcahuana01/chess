package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO{
    public SQLGameDAO() throws  DataAccessException {
        configureDatabase();
    }

    public void clear() throws SQLException, DataAccessException {
        var statement = "TRUNCATE newGame";
        executeUpdate(statement);
    }

    public void createGame(GameData gameData) throws SQLException, DataAccessException {
        var statement = "INSERT INTO newGame (gameId, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        var game = new Gson().toJson(gameData.game());
        executeUpdate(statement, gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
    }

    public GameData readGameData(ResultSet rs) throws SQLException{
        var gameId = rs.getInt("gameId");
        var gameName = rs.getString("gameName");
        var white = rs.getString("whiteUsername");
        var black = rs.getString("blackUsername");
        var game = rs.getString("game");

        var chessGame = new Gson().fromJson(game, ChessGame.class);
        return new GameData(gameId, white, black, gameName, chessGame);
    }

    public GameData getGame(int gameId) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM newGame WHERE gameId=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (var rs = ps.executeQuery()){
                    if (rs.next()){
                        return readGameData(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data");
        }
        return null;
    }

    public void updateGameList(GameData gameData) throws SQLException, DataAccessException {
        var statement = "UPDATE newGame SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameId = ?";
        var game = new Gson().toJson(gameData.game());
        executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(), game, gameData.gameID());
    }


    public Collection<GameData> getAvailableGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM newGame";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()){
                        result.add(readGameData(rs));
                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException("Unable to read data");
        }
        return result;
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()){
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
                for (var i = 0; i < params.length; i++){
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof GameData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
            }
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  newGame (
              `gameId` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` TEXT NOT NULL,
              PRIMARY KEY (`gameId`),
              INDEX(`gameName`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public void configureDatabase() throws  DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()){
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        } catch(SQLException ex){
            throw new DataAccessException("Unable to configure database");
        }
    }
}
