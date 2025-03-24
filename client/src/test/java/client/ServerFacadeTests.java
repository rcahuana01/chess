package client;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertTrue;


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


//    @Test
//    void register() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//
//    }

}
