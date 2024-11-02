package client;

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
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
