package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import static java.lang.System.out;

public class ChessClient implements NotificationHandler {
    private final ServerFacade server;
    private WebSocketFacade ws;
    private final String serverUrl;
    private final Map<Integer, Integer> gameIndexMap;

    private final NotificationHandler notificationHandler;
    private AuthData authData;
    private ChessBoard currentBoard = null;
    private ChessGame currentGame = null;
    private Scanner scanner = new Scanner(System.in);
    private State state = State.SIGNEDOUT;

    // Active game state fields
    private int currentGameId = -1;
    private String currentPlayerColor = "";

    public ChessClient(String serverUrl, NotificationHandler handler) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        this.gameIndexMap = new HashMap<>();
        this.notificationHandler = handler;
        this.serverUrl = serverUrl;
        this.ws = new WebSocketFacade(serverUrl.replace("http", "ws"), this);
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    }

    public String eval(String input) throws DataAccessException {
        var tokens = input.trim().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = tokens.length > 1 ? Arrays.copyOfRange(tokens, 1, tokens.length) : new String[0];

        if (state == State.SIGNEDOUT) {
            return switch(cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> quit();
                case "help" -> helpPreLogin();
                default -> helpPreLogin();
            };
        } else if (state == State.SIGNEDIN) {
            return switch(cmd) {
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> quit();
                case "help" -> helpPostLogin();
                default -> helpPostLogin();
            };
        } else if (state == State.GAMEPLAY) {
            try {
                return switch(cmd) {
                    case "move" -> makeMove(params);
                    case "redraw" -> redrawChessBoard();
                    case "leave" -> leave();
                    case "resign" -> resign();
                    case "highlight" -> highlightMoves(params);
                    case "help" -> helpGamePlay();
                    default -> helpGamePlay();
                };
            } catch (DataAccessException ex) {
                return "Server error: " + ex.getMessage();
            }
        }
        return "";
    }

    private String helpGamePlay() {
        return """
                move <c1,a7> - makes move from c1 to a7
                redraw - redraws the board
                leave - removes you from the game
                resign - forfeits the game and ends it
                highlight <c3> - highlight legal moves from c3
                """;
    }

    private String helpPreLogin() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - exit the program
                help - display commands
                """;
    }

    private String helpPostLogin() {
        return """
                create <NAME> - create a new game
                list - list available games
                join <ID> [WHITE|BLACK] - join a game as a player
                observe <ID> - join a game as an observer
                logout - log out of your account
                quit - exit the program
                help - display commands
                """;
    }

    private String makeMove(String[] params) {
        if (params.length < 1) {
            return "Invalid input. Usage: move <c1,a7>";
        }
        String moveInput = params[0];
        String[] pieces = moveInput.split(",");
        if (pieces.length != 2) {
            return "Invalid move format. Expected format: c1,a7";
        }
        ChessPosition start = parseAlgebraic(pieces[0].trim());
        ChessPosition end = parseAlgebraic(pieces[1].trim());
        if (start == null || end == null) {
            return "Invalid positions in move command.";
        }
        ChessMove move = new ChessMove(start, end, null);
        try {
            ws.makeMove(authData.authToken(), currentGameId, move);
        } catch (DataAccessException e) {
            return "Failed to make move: " + e.getMessage();
        }
        return "Move executed.";
    }

    private String redrawChessBoard() {
        if (currentGame != null) {
            Graphics.drawBoard(out, currentGame, currentPlayerColor.equalsIgnoreCase("black"));
            return "Board redrawn.";
        } else {
            return "No game loaded.";
        }
    }

    private String leave() throws DataAccessException {
        ws.leaveGame(authData.authToken(), currentGameId);
        state = State.SIGNEDIN;
        return "You left the game";
    }

    private String resign() throws DataAccessException {
        out.print("Are you sure you want to resign? [y/n]: ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            ws.resignGame(authData.authToken(), currentGameId);
            return "You resigned the game";
        } else {
            return "Resignation canceled.";
        }

    }

    private String highlightMoves(String[] params) {
        if (params.length < 1) {
            return "Invalid input. Usage: highlight <c3>";
        }
        String input = params[0];
        ChessPosition pos = parseAlgebraic(input);
        if (pos == null) {
            return "Invalid position entered.";
        }
        if (currentBoard != null && currentGame != null) {
            boolean reversed = currentPlayerColor.equalsIgnoreCase("black");
            Graphics.highlightMoves(out, currentGame, pos, reversed);
            return "Highlighted legal moves for " + input;
        } else {
            return "No active game loaded.";
        }
    }

    private ChessPosition parseAlgebraic(String pos) {
        pos = pos.trim().toLowerCase();
        if (pos.length() < 2) {
            return null;
        }
        char file = pos.charAt(0);
        char rank = pos.charAt(1);
        int col = file - 'a' + 1;
        int row = Character.getNumericValue(rank);
        return new ChessPosition(row, col);
    }

    public String create(String... params) throws DataAccessException {
        if (params.length == 0) {
            return "Game name is required.";
        }
        server.create(params[0]);
        state = State.SIGNEDIN;
        return String.format("You created a game as %s.", params[0]);
    }

    public String list() throws DataAccessException {
        Collection<GameData> gameList = server.list();
        int i = 1;
        System.out.printf("%-3s %-15s %-15s %-15s%n", "ID", "Name", "White Player", "Black Player");
        System.out.println("------------------------------------------------------");
        for (GameData game : gameList) {
            gameIndexMap.put(i, game.gameID());
            String whitePlayer = game.whiteUsername() == null ? "N/A" : game.whiteUsername();
            String blackPlayer = game.blackUsername() == null ? "N/A" : game.blackUsername();
            System.out.printf("%-3d %-15s %-15s %-15s%n", i, game.gameName(), whitePlayer, blackPlayer);
            i++;
        }
        state = State.SIGNEDIN;
        return "";
    }

    public String join(String... params) throws DataAccessException {
        if (params.length < 2) {
            return "Error: Missing parameters. Use:  join <ID> [WHITE|BLACK]";
        }

        int index = Integer.parseInt(params[0]);
        if (!gameIndexMap.containsKey(index)) {
            return "Error: Invalid game index.";
        }

        currentGameId = gameIndexMap.get(index);
        server.join(String.valueOf(currentGameId), params[1].toUpperCase());

        currentGame = new ChessGame();
        currentBoard = currentGame.getBoard();
        ws.connect(authData.authToken(), currentGameId, false, params[1].toUpperCase());

        currentPlayerColor = params[1].toUpperCase();
        state = State.GAMEPLAY;
        return String.format("You joined as %s.", params[1]);
    }

    public String observe(String... params) throws DataAccessException {
        if (params.length < 1) {
            return "Game ID is required.";
        }
        int index = Integer.parseInt(params[0]);

        currentGameId = gameIndexMap.get(index);
        ws.connect(authData.authToken(), currentGameId, true, "");

        currentPlayerColor = "";
        state = State.GAMEPLAY;
        return String.format("You joined as observer to game %s.", params[0]);
    }

    public String logout() throws DataAccessException {
        server.logout();
        state = State.SIGNEDOUT;
        return "You logged out.";
    }

    public String login(String... params) throws DataAccessException {
        if (params.length < 2) {
            return "Error: Missing parameters. Use: login <USERNAME> <PASSWORD>";
        }
        authData = server.login(params[0], params[1]);
        state = State.SIGNEDIN;
        return String.format("You logged in as %s.", params[0]);
    }

    public String register(String... params) throws DataAccessException {
        if (params.length < 3) {
            return "Error: Missing parameters. Use: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        try {
            authData = server.register(params[0], params[1], params[2]);
            state = State.SIGNEDIN;
            return String.format("You logged in as %s.", params[0]);
        } catch (DataAccessException e) {
            return "Error: User already exists or registration failed.";
        }
    }

    public String quit() throws DataAccessException {
        out.println("Goodbye!");
        System.exit(0);
        return "";
    }

    @Override
    public void notify(ServerMessage serverMsg) {
        switch (serverMsg.getServerMessageType()) {
            case LOAD_GAME -> {
                this.currentGame = serverMsg.getGame();
                this.currentBoard = (currentGame != null) ? currentGame.getBoard() : null;
                boolean reversed = currentPlayerColor.equalsIgnoreCase("black");
                out.println();
                Graphics.drawBoard(out, currentGame, reversed);

            }
            case NOTIFICATION -> {
                System.out.println("NOTIFICATION: " + serverMsg.getMessage());
            }
            case ERROR -> {
                System.out.println("ERROR: " + serverMsg.getErrorMessage());
            }
        }
    }

}
