package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Spark;

import dataaccess.*;
import service.*;

import java.util.Map;

public class Server {
    private final UserHandler userHandler;
    private final GameHandler gameHandler;
    private final SystemHandler systemHandler;

    public Server() {
        // Instantiate DAOs and Services
        SQLAuthDAO authDAO = new SQLAuthDAO();
        SQLGameDAO gameDAO = new SQLGameDAO();
        SQLUserDAO userDAO = new SQLUserDAO();

        // Instantiate the handlers with their services
        this.userHandler = new UserHandler(new UserService(userDAO, authDAO));
        this.gameHandler = new GameHandler(new GameService(gameDAO, authDAO));
        this.systemHandler = new SystemHandler(new SystemService(userDAO, authDAO, gameDAO));
    }

    public int run(int desiredPort) {
        try {
            Spark.port(desiredPort);

            Spark.staticFiles.location("web");

            Spark.post("/user", userHandler::register);
            Spark.post("/session", userHandler::login);
            Spark.delete("/session", userHandler::logout);
            Spark.get("/game", gameHandler::listGames);
            Spark.post("/game", gameHandler::createGame);
            Spark.put("/game", gameHandler::joinGame);
            Spark.delete("/db", systemHandler::clear);

            Spark.exception(Exception.class, this::errorHandler);
            Spark.notFound((req, res) -> {
                var msg = String.format("[%s] %s not found", req.requestMethod(), req.pathInfo());
                return errorHandler(new Exception(msg), req, res);
            });

        } catch (Exception e) {
            System.out.println("Unable to start server: " + e.getMessage());
            System.exit(1);
        }
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public String errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        res.type("application/json");
        res.status(500);
        res.body(body);
        return body;
    }
}
