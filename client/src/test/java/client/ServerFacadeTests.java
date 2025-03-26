package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private ServerFacade facade;
    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void validRegister() throws Exception {
        facade.register("testUser1", "password", "email1@test.com");
        assertNotNull(auth.authToken());
        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    void invalidRegister() {
        assertDoesNotThrow(() -> facade.register("dupeUser", "pass", "dupe@email.com"));
        assertThrows(Exception.class, () -> facade.register("dupeUser", "pass", "dupe@email.com"));
    }

    @Test
    void validLogin() throws Exception {
        facade.register("testUser2", "password", "email2@test.com");
        facade.logout();
        facade.login("testUser2", "password");
    }

    @Test
    void invalidLogin() {
        assertThrows(Exception.class, () -> facade.login("nonexistent", "wrong"));
    }

    @Test
    void validCreate() throws Exception {
        facade.register("testUser3", "password", "email3@test.com");
        assertDoesNotThrow(() -> facade.create("Cool Game"));
    }

    @Test
    void validList() throws Exception {
        facade.register("testUser4", "password", "email4@test.com");
        facade.create("Another Game");
        assertFalse(facade.list().isEmpty());
    }

    @Test
    void validJoin() throws Exception {
        facade.register("testUser5", "password", "email5@test.com");
        facade.create("GameToJoin");
        var games = facade.list();
        facade.join("1", "WHITE");
    }

    @Test
    void invalidJoin() throws Exception {
        facade.register("testUser6", "password", "email6@test.com");
        assertThrows(Exception.class, () -> facade.join("99", "BLACK")); // Index 99 shouldn't exist
    }

    @Test
    void validObserve() throws Exception {
        facade.register("testUser7", "password", "email7@test.com");
        facade.create("ObserveGame");
        facade.observe("1"); // Should work
    }

    @Test
    void invalidObserve() throws Exception {
        facade.register("testUser8", "password", "email8@test.com");
        assertThrows(Exception.class, () -> facade.observe("999")); // Invalid index
    }

    @Test
    void validLogout() throws Exception {
        facade.register("testUser9", "password", "email9@test.com");
        assertDoesNotThrow(() -> facade.logout());
    }

    @Test
    void validQuit() throws Exception {
        facade.register("testUser10", "password", "email10@test.com");
        assertDoesNotThrow(() -> facade.quit());
    }

    @Test
    void invalidLogout() {
        assertThrows(Exception.class, () -> facade.logout());
    }

    @Test
    void invalidQuit() {
        assertThrows(Exception.class, () -> facade.quit());
    }

}
