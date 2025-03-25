//package client;
//
//import org.junit.jupiter.api.*;
//import server.Server;
//import ui.ServerFacade;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//public class ServerFacadeTests {
//
//    private static Server server;
//    private ServerFacade facade;
//    @BeforeAll
//    public static void init() {
//        server = new Server();
//        var port = server.run(8080);
//        System.out.println("Started test HTTP server on " + port);
//    }
//
//    @AfterAll
//    static void stopServer() {
//        server.stop();
//    }
//
//
//    @Test
//    void validRegister() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue();
//    }
//
//    @Test
//    void invalidRegister() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void validCreate() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void invalidCreate() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void validList() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void invalidList() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void validJoin() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void invalidJoin() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void validObserve() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void invalidObserve() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void validLogout() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void invalidLogout() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void validLogin() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void invalidLogin() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void validQuit() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//
//    @Test
//    void invalidQuit() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }
//}
