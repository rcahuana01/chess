package dataaccess;

import dataaccess.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;

public class SQLUserDAOTest {
    private final SQLUserDAO userDAO = new SQLUserDAO();
    UserData validUserData = new UserData("rcahuana", "hello", "rcahuana@gmail.com");
    UserData invalidUserData = new UserData(null, null, "rcahuana@gmail.com");

    @BeforeEach
    void setUp() throws ResponseException {
        userDAO.clear();
    }

    @AfterEach
    void tearDown() throws ResponseException {
        userDAO.clear();
    }

    @Test
    void positiveCreateUserTest() throws ResponseException {
        userDAO.createUser(validUserData);
    }

    @Test
    void negativeCreateUserTest() {
        ResponseException e = Assertions.assertThrows(ResponseException.class, () -> userDAO.createUser(invalidUserData));
        Assertions.assertEquals(500, e.getStatusCode());
    }

    @Test
    void positiveGetUserTest() throws ResponseException {
        userDAO.createUser(validUserData);
        UserData user = userDAO.getUser(validUserData.username());
        Assertions.assertNotNull(user);
    }

    @Test
    void negativeGetUserTest() throws ResponseException {
        Assertions.assertNull(userDAO.getUser(invalidUserData.username()));
    }

    @Test
    void positiveClearTest() throws ResponseException {
        userDAO.createUser(validUserData);
        userDAO.clear();
    }

}