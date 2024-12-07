package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import model.UserData;
import websocket.commands.*;
import websocket.commands.MakeMove;
import websocket.commands.Resign;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Client implements NotificationHandler{

    private final ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    private ClientState state = ClientState.PRE_LOGIN;
    private Scanner scanner = new Scanner(System.in);
    private AuthData authData = null;
    private int currentGameId = -1;
    private String currentPlayerColor = "";
    private ChessBoard currentBoard = null;
    private ChessGame currentGame = null;
    private ui.websocket.WebSocketFacade webSocket = new ui.websocket.WebSocketFacade("ws://localhost:8080/ws", this);

    public Client() throws DeploymentException, IOException {
    }

    public void displayPreloginCommands() {
        System.out.println("1. \"register\"");
        System.out.println("2. \"login\"");
        System.out.println("3. \"quit\"");
        System.out.println("4. \"help\"");
    }

    public void displayPostloginCommands() {
        System.out.println("1. \"create game\"");
        System.out.println("2. \"list games\"");
        System.out.println("3. \"join game\"");
        System.out.println("4. \"observe game\"");
        System.out.println("5. \"logout\"");
        System.out.println("6. \"quit\"");
        System.out.println("7. \"help\"");
    }

    public void displayIngameCommands() {
        System.out.println("1. \"draw\"");
        System.out.println("2. \"leave\"");
    }

    public void displayObservingCommands() {
        System.out.println("1. \"draw\"");
        System.out.println("2. \"leave\"");
    }

    public void run() throws Exception {
        System.out.println("Welcome to Chess!");
        while (true) {
            System.out.println("Available commands: ");

            switch (state) {
                case PRE_LOGIN -> handlePreloginCommands();
                case POST_LOGIN -> handlePostloginCommands();
                case IN_GAME -> handleIngameCommands();
                case OBSERVING -> handleObservingCommands();
            }
        }
    }

    private void handlePreloginCommands() {
        displayPreloginCommands();
        switch (scanner.nextLine().trim().toLowerCase()) {
            case "1", "register" -> register();
            case "2", "login" -> login();
            case "3", "quit" -> quit();
            case "4", "help" -> helpPrelogin();
            default -> System.out.println("Invalid command. Please enter: register, login, quit, help");
        }
    }

    private void helpPrelogin() {
        System.out.println("register - to create a new account");
        System.out.println("login - to play chess");
        System.out.println("quit -  exit the program");
        System.out.println("help - repeat commands");
    }

    private void handlePostloginCommands() throws Exception {
        displayPostloginCommands();
        switch (scanner.nextLine().trim().toLowerCase()) {
            case "1", "create game" -> createGame();
            case "2", "list games" -> listGames();
            case "3", "join game" -> joinGame(false);
            case "4", "observe game" -> joinGame(true);
            case "5", "logout" -> logout();
            case "6", "quit" -> quit();
            case "7", "help" -> helpPostlogin();
            default ->
                    System.out.println("Invalid command, please enter: create game, list games, join game, observe game, logout, quit, help");
        }
    }

    private void helpPostlogin() {
        System.out.println("create game - create a new game");
        System.out.println("list games - list all existing games");
        System.out.println("join game - join an existing game");
        System.out.println("observe game - join a game as an observer");
        System.out.println("logout - go back to previous menu");
        System.out.println("quit - exit the program");
        System.out.println("help - repeat commands");
    }

    private void handleIngameCommands() throws Exception {
        displayIngameCommands();
        switch (scanner.nextLine().trim().toLowerCase()) {
            case "1", "redraw" -> draw();
            case "2", "leave" -> leave();
            case "3", "make move" -> makeMove();
            case "4", "resign" -> resign();
            case "5", "highlight legal moves" -> highlightMoves();
            case "6", "help" -> helpInGame();
            default ->
                    System.out.println("Invalid command, please enter: draw, leave");
        }
    }

    private void leave() throws Exception{
        try {
            webSocket.sendCommand(new Leave(authData.authToken(), currentGameId));
            state = ClientState.POST_LOGIN;
        } catch(Exception e){
            System.out.println("Unable to leave the game. ");
            System.out.println(e.getMessage());
        }
    }

    private void helpInGame() {
        System.out.println("redraw - redraw the chess board");
        System.out.println("leave - leave the current game");
        System.out.println("make move - move one of your pieces");
        System.out.println("resign - leave and end the game");
        System.out.println("highlight legal moves - redraw board with valid moves for a piece");
        System.out.println("help - repeat commands");
    }

    private void resign() throws Exception{
        try {
            System.out.print("Are you sure you want to resign? [y/n]: ");
            String confirmation = scanner.nextLine();
            if (confirmation.equals("y")){
                webSocket.sendCommand(new Resign(authData.authToken(), currentGameId));
                System.out.println("Game is over!");
            }
        } catch (Exception e){
            System.out.println("Unable to resign the game ");
        }
    }

    private void makeMove() throws Exception{
        try {
            System.out.print("Enter move to execute (e.g., a1-a5): ");
            String moveInput = scanner.nextLine();
            String[] movePositions = moveInput.split("-");
            ChessPosition start = new ChessPosition(-1,1);
            ChessPosition end = new ChessPosition(-1,-1);
            // Move object
            if (start !=null && end !=null){
                ChessMove move = new ChessMove(start,end,null);
                try {
                    webSocket.sendCommand(new MakeMove(authData.authToken(),currentGameId, move));
                } catch (Exception e) {
                    System.out.println("Error making move");
                }
            }
        } catch (Exception e){
            System.out.println("Unable to make move with the information provided.");
            System.out.println(e.getMessage());
        }
    }

    private void handleObservingCommands() {
        displayObservingCommands();
        switch (scanner.nextLine().trim().toLowerCase()) {
            case "1", "redraw" -> draw();
            case "2", "leave" -> state = ClientState.POST_LOGIN;
            case "3", "highlight legal moves" -> highlightMoves();
            case "4", "help" -> helpObserving();
            default -> System.out.println("Invalid command, please enter: draw, leave");
        }
    }

    private void highlightMoves() {
        try {
            if (currentBoard != null && currentGame!=null){
                System.out.println("Enter the position of the piece you want to move: ");
                String positionInput = scanner.nextLine();
                ChessPosition piecePosition = new ChessPosition(-1,-1);
                piecePosition = piecePosition.getPositionFromString(positionInput, currentPlayerColor.toLowerCase(Locale.ROOT).equals("black"));
                ChessBoardBuilder boardBuilder = new ChessBoardBuilder(currentBoard, currentGame);
                boardBuilder.printBoard(currentPlayerColor, piecePosition);
            }
        } catch (Exception e) {
            System.out.println("Unable to highlight valid moves.");
            System.out.println(e.getMessage());
        }
    }

    private void helpObserving() {
        System.out.println("redraw - redraw the chess board");
        System.out.println("leave - leave the current game");
        System.out.println("highlight legal moves - redraw board with valid moves for a piece");
        System.out.println("help - repeat commands");
    }

    private void register() {
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        System.out.println("Enter your email: ");
        String email = scanner.nextLine();

        UserData userData = new UserData(username, password, email);
        try {
            authData = serverFacade.register(userData);
            System.out.println("You have registered and logged in successfully.");
            state = ClientState.POST_LOGIN;
        } catch (Exception e) {
            System.out.println("Unable to register. " + e.getMessage());
        }
    }

    private void login() {
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();

        UserData userData = new UserData(username, password, null);
        try {
            authData = serverFacade.login(userData);
            System.out.println("You have logged in successfully.");
            state = ClientState.POST_LOGIN;
        } catch (Exception e) {
            System.out.println("Unable to login with the information provided.");
            System.out.println(e.getMessage());
        }
    }

    private void createGame() {
        System.out.println("Enter the name of the game: ");
        String gameName = scanner.nextLine();

        GameData gameData = new GameData(0, null, null, gameName, null);
        try {
            serverFacade.createGame(authData.authToken(), gameData);
            System.out.println("Game created successfully!");
        } catch (Exception e) {
            System.out.println("Unable to create game. " + e.getMessage());
        }
    }

    private void listGames() {
        try {
            var games = serverFacade.listGames(authData.authToken());
            if (games != null && !games.games().isEmpty()) {
                System.out.println("List of current games:");
                int gameIDIndex = 1;
                for (GameData game : games.games()) {
                    System.out.printf("Game ID: %d, Name: %s, White Player: %s, Black Player: %s%n",
                            gameIDIndex++, game.gameName(), game.whiteUsername(), game.blackUsername());
                }
            } else {
                System.out.println("No games available.");
            }
        } catch (Exception e) {
            System.out.println("Unable to list games. " + e.getMessage());
        }
    }

    private void joinGame(boolean observer) {
        try {
            var games = serverFacade.listGames(authData.authToken());
            if (games != null && !games.games().isEmpty()) {
                Map<Integer, Integer> gamesIDs = new HashMap<>();
                int gameIDIndex = 1;
                for (GameData game : games.games()) {
                    gamesIDs.put(gameIDIndex, game.gameID());
                    gameIDIndex++;
                }

                System.out.println("Enter the Game ID:");
                int gameId = Integer.parseInt(scanner.nextLine());
                currentGameId = gamesIDs.get(gameId);

                if (!observer) {
                    System.out.println("Enter player color (WHITE/BLACK):");
                    String playerColor = scanner.nextLine().toUpperCase();
                    serverFacade.joinGame(authData.authToken(), currentGameId, playerColor);
                    currentPlayerColor = playerColor;
                    state = ClientState.IN_GAME;
                } else {
                    serverFacade.joinGame(authData.authToken(), currentGameId, "observer");
                    currentPlayerColor = "";
                    state = ClientState.OBSERVING;
                }

                // Initialize board with pieces in starting positions
                currentBoard = new ChessBoard();
                currentBoard.resetBoard();
                currentGame = new ChessGame();
                webSocket.sendCommand(new Connect(authData.authToken(), currentGameId, currentPlayerColor.equalsIgnoreCase(
                        "white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK, observer));
                System.out.println("Joined the game successfully.");
            } else {
                System.out.println("No games available to join.");
            }
        } catch (Exception e) {
            System.out.println("Unable to join with the information provided.");
            System.out.println(e.getMessage());
        }
    }

    private void logout() {
        try {
            serverFacade.logout(authData.authToken());
            state = ClientState.PRE_LOGIN;
            System.out.println("Logged out successfully.");
        } catch (Exception e) {
            System.out.println("Unable to logout. " + e.getMessage());
        }
    }

    private void draw() {
        if (currentBoard != null && currentGame != null) {
            System.out.println("Redrawing the chess board...");

            // Print from white's perspective
            ChessBoardBuilder whitePerspectiveBoard = new ChessBoardBuilder(currentBoard, currentGame);
            System.out.println("White's Perspective:");
            whitePerspectiveBoard.printBoard("WHITE", null);

            // Print from black's perspective
            ChessBoardBuilder blackPerspectiveBoard = new ChessBoardBuilder(currentBoard, currentGame);
            System.out.println("Black's Perspective:");
            blackPerspectiveBoard.printBoard("BLACK", null);

        } else {
            System.out.println("Current board or game is null. Unable to redraw.");
        }
    }

    private void quit() {
        System.out.println("See you soon!");
        System.exit(0);
    }

    @Override
    public void notify(Notification notification) {
        System.out.println("ERROR: " + notification.message);
    }

    @Override
    public void warn(ErrorMessage errorMessage) {
        System.out.println("ERROR: " + errorMessage.errorMessage);
    }

    @Override
    public void loadGame(LoadGame loadGame) {
        this.currentGame = loadGame.game.game();
        this.currentBoard = loadGame.game.game().getBoard();
    }
}
