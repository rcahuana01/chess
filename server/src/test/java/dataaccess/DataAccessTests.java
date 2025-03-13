package dataaccess;

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
    private static AuthData auth = new AuthData("token", "rodrigo");
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
        GameData invalidGame = new GameData(0, null, null, "invalidGame", null);
        gameDAO.createGame(invalidGame);
        GameData retrievedGame = gameDAO.getGame(invalidGame.gameID());
        Assertions.assertNull(retrievedGame);
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
        GameData retrievedGame = gameDAO.getGame(-1);
        Assertions.assertNull(retrievedGame);
    }

    @Test
    public void validUpdateGameList() throws DataAccessException, SQLException {
        gameDAO.createGame(game);
        gameDAO.createGame(game2);

        gameDAO.updateGameList(game2);

        GameData updatedGame = gameDAO.getGame(2);
        Assertions.assertNotNull(updatedGame);
        Assertions.assertEquals(game2.gameName(), updatedGame.gameName());
    }


    @Test
    public void invalidUpdateGameList() throws DataAccessException, SQLException {
        gameDAO.createGame(game);
        gameDAO.createGame(game2);

        GameData invalidGame = new GameData(999, null, null, null, null);
        gameDAO.updateGameList(invalidGame);

        GameData retrievedInvalidGame = gameDAO.getGame(999);
        Assertions.assertNull(retrievedInvalidGame);

        Collection<GameData> availableGames = gameDAO.getAvailableGames();
        Assertions.assertEquals(2, availableGames.size());
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
        userDAO.createUser(user);
        UserData retrievedUser = userDAO.getUser(user.username());
        Assertions.assertNotNull(retrievedUser);
        Assertions.assertEquals("rodrigo", retrievedUser.username());
    }

    @Test
    public void invalidCreateUser() throws SQLException, DataAccessException {
        UserData invalidUser = new UserData(null, null, null);
        Assertions.assertThrows(Exception.class, () -> userDAO.createUser(invalidUser));
    }


    @Test
    public void validGetUser() throws DataAccessException, SQLException {
        userDAO.createUser(user);
        UserData retrievedUser = userDAO.getUser(user.username());
        Assertions.assertEquals("rodrigo", retrievedUser.username());
    }

    @Test
    public void invalidGetUser() throws DataAccessException, SQLException {
        UserData retrievedUser = userDAO.getUser("cosmo");
        Assertions.assertNull(retrievedUser);
    }

    @Test
    public void validClearUser() throws DataAccessException, SQLException {
        userDAO.createUser(user);
        userDAO.clear();
        UserData clearedUser = userDAO.getUser(user.username());
        Assertions.assertNull(clearedUser);
    }

    @Test
    public void validCreateAuth() throws SQLException, DataAccessException {
        authDAO.createAuth(auth);
        AuthData retrievedAuth = authDAO.getAuthToken(auth.authToken());
        Assertions.assertNotNull(retrievedAuth);
        Assertions.assertEquals(auth.username(), retrievedAuth.username());
    }

    @Test
    public void invalidCreateAuth() throws DataAccessException, SQLException {
        AuthData invalidAuthData = new AuthData(null, null);
        Assertions.assertThrows(Exception.class, () -> {
            authDAO.createAuth(invalidAuthData);
        });
    }

    @Test
    public void validDeleteAuth() throws SQLException, DataAccessException {
        authDAO.createAuth(auth);
        authDAO.deleteAuth(auth.authToken());
        AuthData deletedAuth = authDAO.getAuthToken(auth.username());
        Assertions.assertNull(deletedAuth);
    }

    @Test
    public void invalidDeleteAuth() throws DataAccessException{
        AuthData retrievedAuth = authDAO.getAuthToken("none");
        Assertions.assertNull(retrievedAuth);
    }

    @Test
    public void validGetAuthToken() throws DataAccessException, SQLException {
        authDAO.createAuth(auth);
        AuthData retrievedAuth = authDAO.getAuthToken(auth.authToken());
        Assertions.assertEquals(auth.username(), retrievedAuth.username());
    }


    @Test
    public void invalidGetAuthToken() throws DataAccessException{
        AuthData retrievedAuth = authDAO.getAuthToken("empty");
        Assertions.assertNull(retrievedAuth);
    }

    @Test
    public void validClearAuth() throws SQLException, DataAccessException {
        authDAO.createAuth(auth);
        authDAO.clear();
        AuthData clearedAuth = authDAO.getAuthToken(auth.username());
        Assertions.assertNull(clearedAuth);
    }
}
