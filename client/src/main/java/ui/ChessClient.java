package ui;

import com.sun.nio.sctp.NotificationHandler;
import dataaccess.DataAccessException;
import model.GameData;
import model.UserData;

import java.util.Arrays;
import java.util.Collection;

import static javax.swing.text.html.FormSubmitEvent.MethodType.*;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    public ChessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) throws DataAccessException{
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        if (state == State.SIGNEDOUT){
            return switch(cmd){
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> quit();
                case "help" -> helpPreLogin();
                default -> helpPreLogin();
            };
        } else if (state == State.SIGNEDIN){
            return switch(cmd){
                case "create" -> create(params);
                case "list" -> list(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> quit();
                case "help" -> helpPostLogin();
                default -> helpPostLogin();
            };
        }

        return cmd;
    }

    public String create(String... params) throws DataAccessException{
        server.create(params);
        state = State.SIGNEDOUT;
        return String.format("You created a game as %s.", params[3]);
    }

    public String list(String... params) throws DataAccessException{
        server.list();
        state = State.SIGNEDOUT;
        return String.format("You logged out as %s.", params[0]);
    }

    public String join(String... params) throws DataAccessException{
        server.join();
        state = State.SIGNEDOUT;
        return String.format("You joined as %s.", params[0]);
    }

    public String observe(String... params) throws DataAccessException{
        server.observe();
        state = State.SIGNEDOUT;
        return String.format("You joined as observer as %s.", params[0]);
    }

    public String logout(String... params) throws DataAccessException{
        server.logout();
        state = State.SIGNEDOUT;
        return String.format("You logged out as %s.", params[0]);
    }

    public String helpPostLogin() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
    }

    public String login(String ...params) throws DataAccessException{
        if (params.length < 2) {
            return "Error: Missing parameters. Use: login <USERNAME> <PASSWORD>";
        }
        server.login(params[0], params[1]);
        state = State.SIGNEDIN;
        return String.format("You logged in as %s.", params[0]);

    }

    public String register(String ...params) throws DataAccessException {
        if (params.length < 3) {
            return "Error: Missing parameters. Use: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        server.register(params[0], params[1], params[2]);
        state = State.SIGNEDIN;
        return String.format("You logged in as %s.", params[0]);
    }

    public String quit() throws DataAccessException {
        server.quit();
        state = State.SIGNEDOUT;
        return "See you later.";

    }

    public String helpPreLogin() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
    }
}
