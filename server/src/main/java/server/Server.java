package server;
import com.google.gson.Gson;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.JoinRequest;
import model.UserData;
import server.websocket.WebSocketHandler;
import spark.*;

import java.sql.SQLException;
import java.util.*;
import service.*;

public class Server {
    AuthDAO authDAO;

    {
        try {
            authDAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    UserDAO userDAO;

    {
        try {
            userDAO = new SQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    GameDAO gameDAO;

    {
        try {
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final UserService userService = new UserService(userDAO, authDAO);
    private ArrayList<String> names = new ArrayList<>();
    private final WebSocketHandler webSocketHandler;

    public Server() {
        webSocketHandler = new WebSocketHandler();
        GameService gameService=  new GameService(userDAO,  authDAO, gameDAO);

    }


    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("public");
        Spark.webSocket("/ws", webSocketHandler);


        // Register your endpoints and handle exceptions here.

        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/db", this::clear);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.get("/game", this::listGames);
        Spark.exception(DataAccessException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    record ExceptionMessage(String message){  }
    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        if (Objects.equals(ex.getMessage(), "Error: already taken")){
            res.status(403);
        } else if (Objects.equals(ex.getMessage(), "Error: bad request")) {
            res.status(400);

        } else if (Objects.equals(ex.getMessage(), "Error: unauthorized")){
            res.status(401);

        } else {
            res.status(500);
        }
        res.body(new Gson().toJson(new ExceptionMessage(ex.getMessage())));
    }

    private Object register(Request req, Response res) throws DataAccessException, SQLException {
        var user = new Gson().fromJson(req.body(), UserData.class);

        AuthData auth = userService.register(user);
        res.status(200);
        return new Gson().toJson(auth);
    }

    private Object login(Request req, Response res) throws DataAccessException, SQLException {
        var user = new Gson().fromJson(req.body(), UserData.class);

        AuthData auth = userService.login(user);
        res.status(200);
        return new Gson().toJson(auth);
    }

    private Object logout(Request req, Response res) throws DataAccessException, SQLException {
        var authHeader = req.headers("Authorization");
        AuthData auth = userService.logout(authHeader);
        res.status(200);
        return new Gson().toJson(auth);
    }

    private Object createGame(Request req, Response res) throws DataAccessException, SQLException {
        var authHeader = req.headers("Authorization");
        var game = new Gson().fromJson(req.body(), GameData.class);
        GameData gameData = gameService.createGame(game.gameName(), authHeader);
        res.status(200);
        return new Gson().toJson(gameData);
    }

    private Object joinGame(Request req, Response res) throws DataAccessException, SQLException {
        var authHeader = req.headers("Authorization");
        var game = new Gson().fromJson(req.body(), JoinRequest.class);
        GameData gameData = gameService.joinGame(game.gameID(), game.playerColor(), authHeader);
        res.status(200);
        return new Gson().toJson(gameData);
    }

    private Object listGames(Request req, Response res) throws DataAccessException, SQLException {
        var authHeader = req.headers("Authorization");
        Collection<GameData> listGames = gameService.listGames(authHeader);
        res.status(200);
        return new Gson().toJson(Map.of("games", listGames));
    }
    private Object clear(Request req, Response res) throws DataAccessException, SQLException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
        return "";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
