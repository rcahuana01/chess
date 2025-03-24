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


    public String login(String ...params){
        // take 2 params
        if (params.length >= 1) {
            UserData user = new UserData(params[0], params[1], null);
            server.makeRequest("POST", "/user", user, UserData.class);
            return String.format("You signed in as %s.", params[0]);
        }

    }

    public String register(String ...params){

    }

    public String quit(){

    }
    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - signIn <yourname>
                    - quit
                    """;
        }
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID>
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }
}
