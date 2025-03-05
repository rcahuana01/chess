package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ServiceTests {
    private static UserService userService;
    private GameService gameService;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    UserData user = new UserData("rodrigo", "password", "rcahuana@byu.edu");
    GameData game = new GameData(1, null, null, "gameName", new ChessGame());

    @BeforeAll
    public static void setUp(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @BeforeEach
    public void init(){
        gameService = new GameService(userDAO, authDAO,gameDAO);
    }

    @Test
    public void validRegister() throws DataAccessException {
        AuthData authData = UserService.register(user);
        Assertions.assertEquals(user.username(), authData.username());
    }

    @Test
    public void invalidRegister() throws DataAccessException {
        UserData user = new UserData("rodrigo", "password", "null");
        AuthData authData = UserService.register(user);
        Assertions.assertEquals(user.username(), authData.username());
    }

    @Test
    public void validLogin() throws  DataAccessException {
        UserData user = new UserData("rodrigo", "password", "null");
        AuthData authData = UserService.register(user);
        AuthData authData1 = userService.login(user);
        Assertions.assertEquals(user.username(), authData1.username());
    }

    @Test
    public void invalidLogin() throws DataAccessException {
        UserData user = new UserData("null", "password", "null");
        AuthData authData = userService.login(user);
        Assertions.assertEquals(user.username(), authData.username());
    }

    @Test
    public void logout() throws  DataAccessException {
        UserData user = new UserData("null", "password", "null");
        AuthData auth = new AuthData("", user.username());
        AuthData authData = userService.logout(auth.authToken());
        Assertions.assertEquals("null", authData.username());
    }

    @Test
    public void validCreateGame() throws DataAccessException {
        AuthData authData = UserService.register(user);
        Integer gameID = gameService.createGame(game.gameName(), authData.authToken()).gameID();
        Assertions.assertNotNull(gameID);
    }

    @Test
    public void invalidCreateGame() throws  DataAccessException {
        AuthData authData = UserService.register(user);
        Integer gameID = gameService.createGame(game.gameName(), authData.authToken()).gameID();
        Assertions.assertNull(gameID);
    }

    @Test
    public void validJoinGame() throws  DataAccessException{
        AuthData authData = UserService.register(user);
        gameService.joinGame(game.gameID(), "BLACK", authData.authToken());
        Assertions.assertEquals("BLACK", null);
    }

    @Test
    public void invalidJoinGame() throws  DataAccessException{
        AuthData authData = UserService.register(user);
        gameService.joinGame(game.gameID(), "BLACK", authData.authToken());
        Assertions.assertNotEquals("BLACK", null);
    }

    @Test
    public void validListGames() throws DataAccessException{

        AuthData authData = UserService.register(user);
        gameService.createGame(game.gameName(), authData.authToken()).gameID();
        gameService.createGame(game.gameName(), authData.authToken()).gameID();
        Collection<GameData> games = gameService.listGames(authData.authToken());
        Assertions.assertEquals(2, games.size());
    }

    @Test
    public void invalidListGames() throws DataAccessException{
        AuthData authData = UserService.register(user);
        gameService.createGame(game.gameName(), authData.authToken()).gameID();
        gameService.createGame(game.gameName(), authData.authToken()).gameID();
        Collection<GameData> games = gameService.listGames(authData.authToken());
        Assertions.assertEquals(2, games.size());
    }

}
