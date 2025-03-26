package ui;

import com.sun.nio.sctp.NotificationHandler;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.io.PrintStream;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.lang.System.out;
import static javax.swing.text.html.FormSubmitEvent.MethodType.*;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    public ChessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

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
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> quit();
                case "help" -> helpPostLogin();
                default -> helpPostLogin();
            };
        }

        return "";
    }

    public String create(String... params) throws DataAccessException{
        if (params.length == 0) {
            return "Game name is required.";
        }
        server.create(params[0]);
        state = State.SIGNEDIN;
        return String.format("You created a game as %s.", params[0]);
    }

    public String list() throws DataAccessException{
        server.list();
        state = State.SIGNEDIN;
        return "";
    }

    public String join(String... params) throws DataAccessException{
        if (params.length < 2) {
            return "Error: Missing parameters. Use:  join <ID> [WHITE|BLACK]";
        }
        server.join(params[0], params[1].toUpperCase());
        if (params[1].equalsIgnoreCase("WHITE")){
            Graphics.drawBoard(out, false);
        } else if (params[1].equalsIgnoreCase("BLACK")){
            Graphics.drawBoard(out, true);

        }
        state = State.SIGNEDIN;
        return String.format("You joined as %s.", params[1]);

    }

    public String observe(String... params) throws DataAccessException{
        if (params.length == 0) {
            return "Game ID is required.";
        }
        server.observe(params[0]);
        Graphics.drawBoard(out, false);
        state = State.SIGNEDIN;
        return String.format("You joined as observer to game %s.", params[0]);
    }

    public String logout() throws DataAccessException{
        server.logout();
        state = State.SIGNEDOUT;
        return "You logged out.";
    }

    public String helpPostLogin() {
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
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
        try {
            server.register(params[0], params[1], params[2]);
            state = State.SIGNEDIN;
            return String.format("You logged in as %s.", params[0]);
        } catch (DataAccessException e) {
            return "Error: User already exists or registration failed. ";
        }
    }

    public String quit() throws DataAccessException {
        out.println("Goodbye!");
        System.exit(0);
        return "";
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
