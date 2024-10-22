package server;
import com.google.gson.Gson;

import dataaccess.ResponseException;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService){
        this.userService = userService;
    }

    public Object register(Request req, Response res) {
        try {
            UserData user = new Gson().fromJson(req.body(), UserData.class);
            res.status(200);
            return new Gson().toJson(userService.register(user));
        } catch (ResponseException e){
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }

    public Object login(Request req, Response res){
        try {
            UserData user = new Gson().fromJson(req.body(), UserData.class);
            res.status(200);
            return new Gson().toJson(userService.login(user));  // Call the login method on userService
        } catch (ResponseException e){
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }


    public Object logout(Request req, Response res){
        try {
            userService.logout(req.headers("Authorization"));
            res.status(200);
            return "";
        } catch (ResponseException e){
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }
}
