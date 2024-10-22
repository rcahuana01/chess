package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SQLAuthDAOTest {
    private final SQLAuthDAO authDAO = new SQLAuthDAO();
    AuthData validAuthData = new AuthData("token", "rcahuana");
    AuthData invalidAuthData = new AuthData(null, null);

    @BeforeEach
    void setUp() throws ResponseException {
        authDAO.clear();
    }

    @AfterEach
    void tearDown() throws ResponseException {
        authDAO.clear();
    }

    @Test
    void positiveCreateAuthTest() throws ResponseException {
        authDAO.createAuth(validAuthData.username());
    }

    @Test
    void negativeCreateAuthTest() {
        ResponseException e = Assertions.assertThrows(ResponseException.class, () -> authDAO.createAuth(invalidAuthData.username()));
        Assertions.assertEquals(500, e.getStatusCode());
    }

    @Test
    void positiveGetAuthTest() throws ResponseException {
        AuthData auth1 = authDAO.createAuth(validAuthData.username());
        AuthData auth2 = authDAO.getAuth(auth1.authToken());
        Assertions.assertNotNull(auth2);
    }

    @Test
    void negativeGetAuthTest() throws ResponseException {
        Assertions.assertNull(authDAO.getAuth(validAuthData.authToken()));
    }

    @Test
    void positiveDeleteAuthTest() throws ResponseException {
        AuthData auth = authDAO.createAuth(validAuthData.username());
        authDAO.deleteAuth(auth.authToken());
        Assertions.assertNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    void negativeDeleteAuthTest() {
        Assertions.assertDoesNotThrow(() -> authDAO.deleteAuth(invalidAuthData.authToken()));
    }

    @Test
    void positiveClearTest() throws ResponseException {
        authDAO.createAuth(validAuthData.username());
        authDAO.clear();
    }

}