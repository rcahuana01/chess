package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SystemServiceTests {
    private SystemService systemService;

    @BeforeEach
    public void setUp() {
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        systemService = new SystemService(userDAO, authDAO, gameDAO);
    }

    @Test
    public void positiveClearTest() throws ResponseException {
        systemService.clear();
    }
}