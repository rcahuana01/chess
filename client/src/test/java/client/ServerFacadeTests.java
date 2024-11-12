package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;
import ui.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    UserData validUser = new UserData("player1", "password", "p1@email.com");
    UserData invalidUser = new UserData(null, "password", "p2@email.com");
    GameData validGame = new GameData(1, "white", null, "game1", null);
    GameData invalidGame = new GameData(-1, "white", "black", null, null);

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void positiveRegister() throws Exception {
        var authData = serverFacade.register(validUser);
        Assertions.assertNotNull(authData);
    }

    @Test
    void negativeRegister() throws Exception {
        Assertions.assertThrows(Exception.class, () -> serverFacade.register(invalidUser));
    }

    @Test
    void positiveLogin() throws Exception {
        serverFacade.register(validUser);
        var authData = serverFacade.login(validUser);
        Assertions.assertNotNull(authData);
    }

    @Test
    void negativeLogin() throws Exception {
        Assertions.assertThrows(Exception.class, () -> serverFacade.login(invalidUser));
    }

    @Test
    void positiveLogout() throws Exception {
        AuthData authData = serverFacade.register(validUser);
        Assertions.assertThrows(Exception.class, () -> serverFacade.logout(authData.authToken()));
    }

    @Test
    void negativeLogout() throws Exception {
        Assertions.assertThrows(Exception.class, () -> serverFacade.logout(null));
    }

    @Test
    void positiveCreateGame() throws Exception {
        var authData = serverFacade.register(validUser);
        var games = serverFacade.createGame(authData.authToken(), validGame);
        Assertions.assertNotNull(games, "Creating a game should return valid GameData.");
    }

    @Test
    void negativeCreateGame() throws Exception {
        Assertions.assertThrows(Exception.class, () -> serverFacade.createGame(null, validGame));
        var authData = serverFacade.register(validUser);
        Assertions.assertThrows(Exception.class, () -> serverFacade.createGame(authData.authToken(), invalidGame));
    }

    @Test
    void positiveListGames() throws Exception {
        var authData = serverFacade.register(validUser);
        var games = serverFacade.listGames(authData.authToken());
    }

    @Test
    void negativeListGames() throws Exception {
        Assertions.assertThrows(Exception.class, () -> serverFacade.listGames(null));
    }

    @Test
    void positiveJoinGame() throws Exception {
        var authData = serverFacade.register(validUser);
        var games = serverFacade.createGame(authData.authToken(), validGame);
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(authData.authToken(), games.gameID(), "BLACK"));
    }

    @Test
    void negativeJoinGame() throws Exception {
        var authData = serverFacade.register(validUser);
        var games = serverFacade.createGame(authData.authToken(), validGame);
        Assertions.assertThrows(Exception.class, () -> serverFacade.joinGame(authData.authToken(), games.gameID(), "WHITE"));
    }

    @Test
    void positiveClear() throws Exception {

    }

}
