package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Collection;

public class DataAccessTests {
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;

    private static UserData user = new UserData("rodrigo", "password", "rcahuana@byu.edu");
    private static GameData game = new GameData(1, null, null, "gameName", new ChessGame());

    @BeforeAll
    public static void init() {
        try {
            userDAO = new SQLUserDAO();
            gameDAO = new SQLGameDAO();
            authDAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void tearDown() throws SQLException, DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    public void validCreateGame() throws DataAccessException, SQLException {
        GameData createdGame = gameDAO.createGame(game);
        Assertions.assertNotNull(createdGame);
        Assertions.assertNotNull(createdGame.gameID());
    }

    @Test
    public void invalidCreateGame() throws DataAccessException, SQLException {
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameService.createGame(null, "invalidAuthToken");
        });
    }

    @Test
    public void validGetGame() throws DataAccessException, SQLException {
    }

    @Test
    public void invalidGetGame() throws DataAccessException, SQLException {
    }



    @Test
    public void validUpdateGameList() throws DataAccessException, SQLException {
    }

    @Test
    public void invalidUpdateGameList() throws DataAccessException, SQLException {
    }

    @Test
    public void validGetAvailableGames() throws DataAccessException, SQLException {
    }

    @Test
    public void invalidGetAvailableGames() throws DataAccessException, SQLException {
    }

    @Test
    public void validClearGame() throws DataAccessException, SQLException {
    }

    @Test
    public void validCreateUser() throws DataAccessException, SQLException {
    }

    @Test
    public void invalidCreateUser() throws DataAccessException, SQLException {
    }

    @Test
    public void validGetUser() throws DataAccessException, SQLException {
    }

    @Test
    public void invalidGetUser() throws DataAccessException, SQLException {
    }

    @Test
    public void validClearUser() throws DataAccessException, SQLException {
    }




}
