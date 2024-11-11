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
        var authData = serverFacade.login(validUser);
        Assertions.assertThrows(Exception.class, () -> serverFacade.login(invalidUser));
    }

    @Test
    void negativeLogin() throws Exception {
        Assertions.assertThrows(Exception.class, () -> serverFacade.login(invalidUser));
    }

    @Test
    void positiveLogout() throws Exception {
        AuthData authData = serverFacade.register(validUser);
        Assertions.assertThrows(Exception.class, () -> serverFacade.logout(invalidUser));
    }

    @Test
    void negativeLogout() throws Exception {
        Assertions.assertThrows(Exception.class, () -> serverFacade.logout(null));
    }

    @Test
    void positiveCreateGame() throws Exception {
        var authData = serverFacade.register(validUser);
        var games = serverFacade.createGame(authData.authToken(), validGame)
        Assertions.assertNotNull(gameData, "Creating a game should return valid GameData.");
    }

    @Test
    void negativeCreateGame() throws Exception {
        AuthData authToken = new AuthData(authToken, invalidUser);
        ChessGame InvalidGame = null;
        Assertions.assertThrows(Exception.class, () -> serverFacade.createGame(authToken, InvalidGame),
                "Creating a game with invalid data should throw an exception.");
    }

    @Test
    void negativeLogin() throws Exception {
        Assertions.assertThrows(Exception.class, () -> serverFacade.login(invalidUser));
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
