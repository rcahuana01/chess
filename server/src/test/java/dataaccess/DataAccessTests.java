package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class DataAccessTests {
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;

    private static UserData user = new UserData("rodrigo", "password", "rcahuana@byu.edu");
    private static GameData game = new GameData(1, null, null, "gameName", new ChessGame());
    private static GameData game2 = new GameData(2, null, null, "gameName2", new ChessGame());

    @BeforeAll
    public static void init() {
        try {
            userDAO = new SQLUserDAO();
            gameDAO = new SQLGameDAO();
            authDAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void tearDown() throws SQLException, DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    public void validCreateGame() throws DataAccessException, SQLException {
        gameDAO.createGame(game);
        GameData retrievedGame = gameDAO.getGame(game.gameID());
        Assertions.assertNotNull(retrievedGame);
        Assertions.assertEquals(1, retrievedGame.gameID());
        Assertions.assertEquals("gameName", retrievedGame.gameName());
    }

    @Test
    public void invalidCreateGame() throws DataAccessException, SQLException {
        gameDAO.createGame(game);
        Assertions.assertThrows(DataAccessException.class, ()-> gameDAO.createGame(game));
    }

    @Test
    public void validGetGame() throws DataAccessException, SQLException {
        gameDAO.createGame(game);
        GameData retrievedGame = gameDAO.getGame(game.gameID());
        Assertions.assertNotNull(retrievedGame);
        Assertions.assertEquals(1, game.gameID());
    }

    @Test
    public void invalidGetGame() throws DataAccessException, SQLException {
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.getGame(-1);
        });
    }


    @Test
    public void validUpdateGameList() throws DataAccessException, SQLException {
        gameDAO.createGame(game);
        gameDAO.createGame(game2);
        gameDAO.updateGameList(game2);

        int count = 0;
        try (var conn = DatabaseManager.getConnection()) {
            String query = "SELECT COUNT(*) FROM games";
            try (var preparedStatement = conn.prepareStatement(query)) {
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        }
        Assertions.assertEquals(2, count);
    }


    @Test
    public void invalidUpdateGameList() throws DataAccessException, SQLException {
        gameDAO.createGame(game);
        int initialCount = 0;

        try (var conn = DatabaseManager.getConnection()){
            String query = "SELECT COUNT(*) FROM games";
            try (var preparedStatement = conn.prepareStatement(query)){
                var rs = preparedStatement.executeQuery();
                if (rs.next()){
                    initialCount = rs.getInt(1);
                }
            }
        }
        GameData invalidGame = new GameData(999, null, null, null, null);
        gameDAO.updateGameList(invalidGame);
        int finalCount = 0;
        try (var conn = DatabaseManager.getConnection()){
            String query = "SELECT COUNT(*) FROM games";
            try (var preparedStatement = conn.prepareStatement(query)){
                var rs = preparedStatement.executeQuery();
                if (rs.next()){
                    finalCount = rs.getInt(1);
                }
            }
        }
        Assertions.assertEquals(initialCount, finalCount);

    }

    @Test
    public void validGetAvailableGames() throws DataAccessException, SQLException {
        gameDAO.createGame(game);
        gameDAO.createGame(game2);
        Collection<GameData> availableGames = gameDAO.getAvailableGames();
        Assertions.assertNotNull(availableGames);
        Assertions.assertFalse(availableGames.isEmpty());
    }

    @Test
    public void invalidGetAvailableGames() throws DataAccessException, SQLException {
        gameDAO.clear();
        Collection<GameData> availableGames = gameDAO.getAvailableGames();
        Assertions.assertNotNull(availableGames);
        Assertions.assertTrue(availableGames.isEmpty());
    }

    @Test
    public void validClearGame() throws DataAccessException, SQLException {
        gameDAO.createGame(game);
        gameDAO.clear();
        GameData clearedGame = gameDAO.getGame(game.gameID());
        Assertions.assertNull(clearedGame);
    }

    @Test
    public void validCreateUser() throws DataAccessException, SQLException {
    }

    @Test
    public void invalidCreateUser() throws DataAccessException, SQLException {
    }

    @Test
    public void validGetUser() throws DataAccessException, SQLException {
    }

    @Test
    public void invalidGetUser() throws DataAccessException, SQLException {
    }

    @Test
    public void validClearUser() throws DataAccessException, SQLException {
    }




}
