package server;
import com.google.gson.Gson;
import dataaccess.exceptions.ResponseException;
import service.SystemService;
import spark.Request;
import spark.Response;
import java.util.Map;

public class SystemHandler {

    private SystemService systemService;

    public SystemHandler(SystemService systemService){
        this.systemService = systemService;
    }

    public Object clear(Request req, Response res) {
        try {
            systemService.clear();
            res.status(200);
            return " ";
        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }

}
