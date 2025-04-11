package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import websocket.commands.UserGameCommand;

import java.io.PrintStream;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.lang.System.out;
import static javax.swing.text.html.FormSubmitEvent.MethodType.*;

public class ChessClient {
    private final ServerFacade server;
    private WebSocketFacade ws;
    private String authToken;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private State state = State.SIGNEDOUT;
    public ChessClient(String serverUrl, NotificationHandler handler){
        server = new ServerFacade(serverUrl);
        this.notificationHandler = handler;
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
        } else if (state == State.GAMEPLAY){
            return switch (cmd){
                case "move" -> makeMove(params);
                case "redraw" -> redrawChessBoard(params);
                case "leave" -> leave(params);
                case "resign" -> resign(params);
                case "highlight" -> highlightMoves(params);
                case "help" -> helpGamePlay();
                default -> helpGamePlay();

            };
        }

        return "";
    }

    private String helpGamePlay() {
        return """
                move <c1,a7> - makes move from c1 to a7
                redraw - redraws the board
                leave - removes the user from the game
                resign - forfeits the game and the game is over
                highlight <c3> -  highlight legal moves from c3
                """;
    }

    private String highlightMoves(String... params) {
        int gameId = Integer.parseInt(params[0]);
        return "";
    }

    private String resign(String... params) throws DataAccessException {
        if (params.length == 0) {
            return "Game ID is required for resigning.";
        }
        int gameId = Integer.parseInt(params[0]);
        ws.resignGame(authToken, gameId);
        return "You resigned the game";
    }

    private String leave(String[] params) throws DataAccessException {
        int gameId = Integer.parseInt(params[0]);
        ws.leaveGame(authToken, gameId);
        return "You left the game";

    }

    private String redrawChessBoard(String[] params) {
        if (params[1].equalsIgnoreCase("WHITE")){
            Graphics.drawBoard(out, new ChessGame(),false);
        } else if (params[1].equalsIgnoreCase("BLACK")){
            Graphics.drawBoard(out, new ChessGame(),true);

        }
        return "";

    }

    private String makeMove(String[] params) {
        if (!params[0].contains(",")) {
            return "Error: Move format is invalid. Use: move <start,end>";
        }

        String[] squares = params[0].split(",");
        ChessPosition start = parseAlgebraic(squares[0]);
        ChessPosition end = parseAlgebraic(squares[1]);
        ChessMove move = new ChessMove(start, end, null);
        int gameId = Integer.parseInt(params[1]);

        try {
            ws.makeMove(authToken, gameId, move);
        } catch (DataAccessException e) {
            return "Failed to make move: " + e.getMessage();
        }
        return "You made a move";
    }


    private ChessPosition parseAlgebraic(String pos){
        char file = pos.charAt(0);
        char rank = pos.charAt(1);
        int col = file - 'a' + 1;
        int row = Character.getNumericValue(rank);
        return new ChessPosition(row, col);
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
            Graphics.drawBoard(out, new ChessGame(),false);
        } else if (params[1].equalsIgnoreCase("BLACK")){
            Graphics.drawBoard(out, new ChessGame(),true);

        }
        ws = new WebSocketFacade(serverUrl, notificationHandler);
        ws.connect(authToken, Integer.parseInt(params[0]));
        state = State.GAMEPLAY;
        return String.format("You joined as %s.", params[1]);

    }

    public String observe(String... params) throws DataAccessException{
        if (params.length == 0) {
            return "Game ID is required.";
        }
        Graphics.drawBoard(out, new ChessGame(),false); // white perspective
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
