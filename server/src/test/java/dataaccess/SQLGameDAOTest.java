package dataaccess;

import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAOTest {
    private final SQLGameDAO gameDAO = new SQLGameDAO();
    GameData validGameData = new GameData(0, "white0", "black0", "game0", null);
    GameData invalidGameData = new GameData(-1, null, null, null, null);

    @BeforeEach
    void setUp() throws ResponseException {
        gameDAO.clear();
    }

    @AfterEach
    void tearDown() throws ResponseException {
        gameDAO.clear();
    }

    @Test
    void positiveCreateGameTest() throws ResponseException {
        gameDAO.createGame(validGameData.gameName());
    }

    @Test
    void negativeCreateGameTest() {
        ResponseException e = Assertions.assertThrows(ResponseException.class, () -> gameDAO.createGame(invalidGameData.gameName()));
        Assertions.assertEquals(500, e.getStatusCode());
    }

    @Test
    void positiveGetGameTest() throws ResponseException {
        int gameId = gameDAO.createGame(validGameData.gameName());
        Assertions.assertNotNull(gameDAO.getGame((gameId)));
    }

    @Test
    void negativeGetGameTest() throws ResponseException {
        Assertions.assertNull(gameDAO.getGame(validGameData.gameID()));
    }

    @Test
    void positiveListGamesTest() throws ResponseException {
        gameDAO.createGame(validGameData.gameName());
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertNotNull(games);
    }

    @Test
    void negativeListGamesTest() throws ResponseException {
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertEquals(games, new ArrayList<>());
    }

    @Test
    void positiveUpdateGameTest() throws ResponseException {
        int gameId = gameDAO.createGame(validGameData.gameName());
        GameData updatedGameData = new GameData(gameId, "white1", "black1", "game1", null);
        gameDAO.updateGame(updatedGameData);
        GameData retrievedGame = gameDAO.getGame(gameId);
        Assertions.assertEquals(updatedGameData.whiteUsername(), retrievedGame.whiteUsername());
        Assertions.assertEquals(updatedGameData.blackUsername(), retrievedGame.blackUsername());
        Assertions.assertEquals(updatedGameData.gameName(), retrievedGame.gameName());
    }

    @Test
    void negativeUpdateGameTest() throws ResponseException {
        Assertions.assertThrows(Exception.class, () -> gameDAO.updateGame(null));
    }

    @Test
    void positiveClearTest() throws ResponseException {
        gameDAO.createGame(validGameData.gameName());
        gameDAO.clear();
    }
}