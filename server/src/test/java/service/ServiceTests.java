package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Collection;

public class ServiceTests {
    private static UserService userService;
    private static GameService gameService;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;

    private static UserData user = new UserData("rodrigo", "password", "rcahuana@byu.edu");
    private static GameData game = new GameData(1, null, null, "gameName", new ChessGame());

    @BeforeEach
    public void init() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(userDAO, authDAO, gameDAO);
    }

    @Test
    public void validRegister() throws DataAccessException, SQLException {
        AuthData authData = userService.register(user);
        Assertions.assertNotNull(authData);
        Assertions.assertEquals(user.username(), authData.username());
    }

    @Test
    public void invalidRegister() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.register(new UserData("rodrigo", "password", null));
        });
    }

    @Test
    public void validLogin() throws DataAccessException, SQLException {
        userService.register(user);
        AuthData authData = userService.login(user);
        Assertions.assertNotNull(authData);
        Assertions.assertEquals(user.username(), authData.username());
    }

    @Test
    public void invalidLogin() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.login(new UserData("wrongUser", "password", "null"));
        });
    }

    @Test
    public void validLogout() throws DataAccessException, SQLException {
        AuthData authData = userService.register(user);
        userService.logout(authData.authToken());
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout(authData.authToken()));
    }

    @Test
    public void invalidLogout() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.logout("invalidAuthToken");
        });
    }

    @Test
    public void validCreateGame() throws DataAccessException, SQLException {
        AuthData authData = userService.register(user);
        GameData createdGame = gameService.createGame("newGame", authData.authToken());
        Assertions.assertNotNull(createdGame);
        Assertions.assertNotNull(createdGame.gameID());
    }

    @Test
    public void invalidCreateGame() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameService.createGame(null, "invalidAuthToken");
        });
    }

    @Test
    public void validJoinGame() throws DataAccessException, SQLException {
        AuthData authData = userService.register(user);
        GameData createdGame = gameService.createGame("NewGame", authData.authToken());

        GameData updatedGame = gameService.joinGame(createdGame.gameID(), "BLACK", authData.authToken());
        Assertions.assertEquals("BLACK", "BLACK");
    }

    @Test
    public void invalidJoinGame() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(99999, "BLACK", "invalidAuthToken");
        });
    }

    @Test
    public void validListGames() throws DataAccessException, SQLException {
        AuthData authData = userService.register(user);
        gameService.createGame("Game1", authData.authToken());
        gameService.createGame("Game2", authData.authToken());

        Collection<GameData> games = gameService.listGames(authData.authToken());
        Assertions.assertEquals(2, games.size());
    }

    @Test
    public void invalidListGames() throws DataAccessException{
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameService.listGames("invalidAuthToken");
        });
    }

    @Test
    public void clear() throws DataAccessException, SQLException {
        AuthData authData = userService.register(user);
        gameService.createGame("Game1", authData.authToken());
        gameService.createGame("Game2", authData.authToken());
        gameService.clear();
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(user));
        Assertions.assertThrows(DataAccessException.class, () -> gameService.listGames(authData.authToken()));
    }

}
