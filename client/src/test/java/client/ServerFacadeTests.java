package client;


import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;
    UserData validUser = new UserData("testUser1", "password", "email1@test.com");
    UserData invalidUser = new UserData(null, "password", "email1@test.com");
    GameData validGame = new GameData(1, "WHITE", null, "game1", null);
    GameData invalidGame = new GameData(-1, "WHITE", null, null, null);

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0); // use random port
        System.out.println("Started test HTTP server on port " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        facade.clear();
    }

    @Test
    void validRegister() throws Exception {
        assertDoesNotThrow(() -> facade.register(validUser.username(), validUser.password(), validUser.email()));
        facade.create(validGame.gameName());
        Collection<GameData> games = facade.list();
        assertFalse(games.isEmpty(), "Should be able to list games after registering");
    }


    @Test
    void invalidRegister() {
        assertThrows(Exception.class, () ->
                facade.register(invalidUser.username(), invalidUser.password(), invalidUser.email()));
    }

    @Test
    void validLogin() throws Exception {
        facade.register(validUser.username(), validUser.password(), validUser.email());
        facade.logout();
        assertDoesNotThrow(() -> facade.login(validUser.username(), validUser.password()));
    }

    @Test
    void invalidLogin() {
        assertThrows(Exception.class, () -> facade.login("nonexistent", "wrong"));
    }

    @Test
    void validCreate() throws Exception {
        facade.register(validUser.username(), validUser.password(), validUser.email());
        assertDoesNotThrow(() -> facade.create(validGame.gameName()));
    }

    @Test
    void invalidCreate() throws Exception {
        facade.register(validUser.username(), validUser.password(), validUser.email());
        assertThrows(Exception.class, () -> facade.create(invalidGame.gameName())); // null name
    }

    @Test
    void validList() throws Exception {
        facade.register(validUser.username(), validUser.password(), validUser.email());
        facade.create(validGame.gameName());
        var games = facade.list();
        assertNotNull(games);
        assertFalse(games.isEmpty());
    }

    @Test
    void invalidList() throws Exception {
        Assertions.assertThrows(Exception.class, () -> facade.list());
    }

    @Test
    void validJoin() throws Exception {
        facade.register(validUser.username(), validUser.password(), validUser.email());
        facade.create(validGame.gameName());
        facade.list();
        assertDoesNotThrow(() -> facade.join("1", validGame.whiteUsername()));
    }

    @Test
    void invalidJoin() throws Exception {
        facade.register(validUser.username(), validUser.password(), validUser.email());
        assertThrows(Exception.class, () -> facade.join(String.valueOf(invalidGame.gameID()), "BLACK"));
    }

    @Test
    void validLogout() throws Exception {
        facade.register(validUser.username(), validUser.password(), validUser.email());
        assertDoesNotThrow(() -> facade.logout());
    }

    @Test
    void invalidLogout() {
        assertThrows(Exception.class, () -> facade.logout());
    }

}





