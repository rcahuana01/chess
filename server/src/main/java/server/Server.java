package server;
import com.google.gson.Gson;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import spark.*;
import java.util.*;
import service.*;

public class Server {
    AuthDAO authDAO = new MemoryAuthDAO();
    UserDAO userDAO = new MemoryUserDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    private final UserService userService = new UserService(userDAO, authDAO);
//    GameService gameService=  new GameService(userDAO,  authDAO, gameDAO);
    private ArrayList<String> names = new ArrayList<>();



    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.post("/user", this::register);
        Spark.post("/session", this::login);

        Spark.exception(DataAccessException.class, this::exceptionHandler);
//        Spark.post("/session", this::listNames);
//        Spark.delete("/name/:name", this::deleteName);
        Spark.delete("/db", this::clear);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }
    record exceptionMessage(String message){  }
    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        if (Objects.equals(ex.getMessage(), "Error: already taken")){
            res.status(403);
        } else if (Objects.equals(ex.getMessage(), "Error: bad request")) {
            res.status(400);

        } else if (Objects.equals(ex.getMessage(), "Error: unauthorized")){
            res.status(401);

        }else {
            res.status(500);
        }
        res.body(new Gson().toJson(new exceptionMessage(ex.getMessage())));
    }

    private Object register(Request req, Response res) throws DataAccessException {
        var user = new Gson().fromJson(req.body(), UserData.class);

        AuthData auth = userService.register(user);
        res.status(200);
        return new Gson().toJson(auth);
    }

    private Object login(Request req, Response res) throws DataAccessException {
        var user = new Gson().fromJson(req.body(), UserData.class);

        AuthData auth = userService.login(user);
        res.status(200);
        return new Gson().toJson(auth);
    }

    private Object listNames(Request req, Response res) {
        res.type("application/json");
        return new Gson().toJson(Map.of("name", names));
    }

    private Object clear(Request req, Response res) {
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
