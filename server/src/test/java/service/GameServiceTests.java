package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.Collection;

public class GameServiceTests {
    UserData userData = new UserData("rcahuana", "password", "rcahuana@gmail.com");
    GameData validGameData = new GameData(1, null, null, "game0", new ChessGame());
    private GameService gameService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void positiveCreateGameTest() throws ResponseException {
        AuthData authData = userService.register(userData);
        Integer gameId = gameService.createGame(authData.authToken(), validGameData);
        Assertions.assertNotNull(gameId);
    }

    @Test
    public void negativeCreateGameTest() {
        Assertions.assertThrows(ResponseException.class, () -> gameService.createGame(null, validGameData));
    }

    @Test
    public void positiveListGamesTest() throws ResponseException {
        AuthData authData = userService.register(userData);
        gameService.createGame(authData.authToken(), validGameData);
        Collection<GameData> games = gameService.listGames(authData.authToken());
        Assertions.assertNotNull(games);
    }

    @Test
    public void negativeListGamesTest() {
        Assertions.assertThrows(ResponseException.class, () -> gameService.listGames(null));
    }

    @Test
    public void positiveJoinGameTest() throws ResponseException {
        AuthData authData = userService.register(userData);
        Integer gameId = gameService.createGame(authData.authToken(), validGameData);
        gameService.joinGame(authData.authToken(), "BLACK", gameId);
    }

    @Test
    public void negativeJoinGameTest() {
        Assertions.assertThrows(ResponseException.class, () -> gameService.joinGame(null, "WHITE", 20));
    }
}