package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTests {
    UserData validUserData = new UserData("rcahuana", "password", "rcahuana@gmail.com");
    UserData invalidUserData = new UserData(null, "password", null);
    private UserService userService;

    @BeforeEach
    public void setUp() throws ResponseException {
        UserDAO userDao = new SQLUserDAO();
        userDao.clear();
        AuthDAO authDao = new MemoryAuthDAO();
        userService = new UserService(userDao, authDao);
    }

    @Test
    public void positiveRegisterTest() throws ResponseException {
        userService.register(validUserData);
        Assertions.assertNotNull(validUserData);
    }

    @Test
    public void negativeRegisterTest() {
        Assertions.assertThrows(ResponseException.class, () -> userService.register(invalidUserData));
    }

    @Test
    public void positiveLoginTest() throws ResponseException {
        userService.register(validUserData);
        var authData = userService.login(validUserData);
        Assertions.assertNotNull(authData);
    }

    @Test
    public void negativeLoginTest() {
        Assertions.assertThrows(ResponseException.class, () -> userService.login(invalidUserData));
    }

    @Test
    public void positiveLogoutTest() throws ResponseException {
        AuthData authData = userService.register(validUserData);
        String authToken = authData.authToken();
        userService.logout(authToken);
    }

    @Test
    public void negativeLogoutTest() {
        Assertions.assertThrows(ResponseException.class, () -> userService.logout(null));
    }
}