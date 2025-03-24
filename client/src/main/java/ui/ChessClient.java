package ui;

import com.sun.nio.sctp.NotificationHandler;
import dataaccess.DataAccessException;
import model.UserData;

import java.util.Arrays;

import static javax.swing.text.html.FormSubmitEvent.MethodType.*;

public class ChessClient {
    private final String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final State state = State.SIGNEDOUT;
    public ChessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) throws DataAccessException{
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch(cmd){
            case "login" -> login(params);
            case "register" -> register(params);
            case "help" -> help();
            case "quit" -> quit();
            default -> help();
        };
    }

    public String login(String ...params) throws DataAccessException{
        if (params.length >= 1) {
            UserData user = new UserData(params[0], params[1], null);
            server.makeRequest("POST", "/user", user, UserData.class);
            return String.format("You signed in as %s.", params[0]);
        }
        return "";
    }

    public String register(String ...params){

        return "";
    }

    public String quit(){

        return "";
    }
    public String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
    }
}
