package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;

public class ServiceTests {
    private static UserService userService;
    @BeforeAll
    public static void setUp(){
        Server server = new Server();
        server.run(8080);
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);


    }

    @Test
    public void ValidRegister() throws DataAccessException {
        UserData user = new UserData("rodrigo", "password", "rcahuana@byu.edu");
        AuthData authData = userService.register(user);
        Assertions.assertEquals(user.username(), authData.username());
    }

    @Test
    public void InvalidRegister() throws DataAccessException {
        UserData user = new UserData("rodrigo", "password", "null");
        AuthData authData = userService.register(user);
        Assertions.assertEquals(user.username(), authData.username());
    }

    @Test
    public void ValidLogin() throws  DataAccessException {
        UserData user = new UserData("rodrigo", "password", "null");
        AuthData authData = userService.register(user);
        AuthData authData1 = userService.login(user);
        Assertions.assertEquals(user.username(), authData1.username());
    }

    @Test
    public void InvalidLogin() throws DataAccessException {
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
}
