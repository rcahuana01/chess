package ui;

import chess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import ui.websocket.WebSocketFacade;
import websocket.commands.Connect;
import websocket.commands.Leave;
import websocket.commands.MakeMove;
import websocket.commands.Resign;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Client implements NotificationHandler {
    private final ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    private final WebSocketFacade webSocket = new WebSocketFacade("ws://localhost:8080/ws", this);
    private final Scanner scanner = new Scanner(System.in);

    private ClientState state = ClientState.PRE_LOGIN;
    private AuthData authData = null;
    private int currentGameId = -1;
    private String currentPlayerColor = "";
    private ChessBoard currentBoard = null;
    private ChessGame currentGame = null;

    public Client() throws DeploymentException, IOException {
        // Constructor for any initialization
    }

    public void run() throws Exception {
        System.out.println("Welcome to Chess!");
        while (true) {
            System.out.println("Available commands: ");
            switch (state) {
                case PRE_LOGIN -> handlePreLoginCommands();
                case POST_LOGIN -> handlePostLoginCommands();
                case IN_GAME -> handleInGameCommands();
                case OBSERVING -> handleObservingCommands();
            }
        }
    }

    // ** Command Handlers **
    private void handlePreLoginCommands() {
        displayPreLoginCommands();
        switch (scanner.nextLine().trim().toLowerCase()) {
            case "1", "register" -> register();
            case "2", "login" -> login();
            case "3", "quit" -> quit();
            case "4", "help" -> helpPreLogin();
            default -> System.out.println("Invalid command. Please enter: register, login, quit, help");
        }
    }

    private void handlePostLoginCommands() throws Exception {
        displayPostLoginCommands();
        switch (scanner.nextLine().trim().toLowerCase()) {
            case "1", "create game" -> createGame();
            case "2", "list games" -> listGames();
            case "3", "join game" -> joinGame(false);
            case "4", "observe game" -> joinGame(true);
            case "5", "logout" -> logout();
            case "6", "quit" -> quit();
            case "7", "help" -> helpPostLogin();
            default -> System.out.println("Invalid command, please enter: create game, list games, join game, observe game, logout, quit, help");
        }
    }

    private void handleInGameCommands() throws Exception {
        displayInGameCommands();
        switch (scanner.nextLine().trim().toLowerCase()) {
            case "1", "redraw" -> redraw();
            case "2", "leave" -> leave();
            case "3", "make move" -> makeMove();
            case "4", "resign" -> resign();
            case "5", "highlight legal moves" -> highlightMoves();
            case "6", "help" -> helpInGame();
            default -> System.out.println("Invalid command, please enter: redraw, leave, make move, resign, highlight legal moves, help");
        }
    }

    private void handleObservingCommands() throws Exception {
        displayObservingCommands();
        switch (scanner.nextLine().trim().toLowerCase()) {
            case "1", "redraw" -> redraw();
            case "2", "leave" -> leaveToPostLogin();
            case "3", "highlight legal moves" -> highlightMoves();
            case "4", "help" -> helpObserving();
            default -> System.out.println("Invalid command, please enter: redraw, leave, highlight legal moves, help");
        }
    }

    // ** Display Command Menus **
    private void displayPreLoginCommands() {
        System.out.println("1. \"register\"");
        System.out.println("2. \"login\"");
        System.out.println("3. \"quit\"");
        System.out.println("4. \"help\"");
    }

    private void displayPostLoginCommands() {
        System.out.println("1. \"create game\"");
        System.out.println("2. \"list games\"");
        System.out.println("3. \"join game\"");
        System.out.println("4. \"observe game\"");
        System.out.println("5. \"logout\"");
        System.out.println("6. \"quit\"");
        System.out.println("7. \"help\"");
    }

    private void displayInGameCommands() {
        System.out.println("1. \"redraw\"");
        System.out.println("2. \"leave\"");
        System.out.println("3. \"make move\"");
        System.out.println("4. \"resign\"");
        System.out.println("5. \"highlight legal moves\"");
        System.out.println("6. \"help\"");
    }

    private void displayObservingCommands() {
        System.out.println("1. \"redraw\"");
        System.out.println("2. \"leave\"");
        System.out.println("3. \"highlight legal moves\"");
        System.out.println("4. \"help\"");
    }

    // ** Core Functionalities **
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

    private void joinGame(boolean observer) throws Exception {
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

                webSocket.sendCommand(new Connect(
                        authData.authToken(),
                        currentGameId,
                        currentPlayerColor.equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK,
                        observer));

                System.out.println("Joined the game successfully.");
            } else {
                System.out.println("No games available to join.");
            }
        } catch (Exception e) {
            System.out.println("Unable to join the game. Reason:");
            String errorMessage = e.getMessage();

            if (errorMessage.contains("403")) {
                System.out.println("The chosen color is already taken. Please try observing the game.");
            } else if (errorMessage.contains("404")) {
                System.out.println("The game you are trying to join does not exist.");
            } else if (errorMessage.contains("401")) {
                System.out.println("You are not authorized to join this game. Please log in again.");
            } else {
                System.out.println("An unexpected error occurred: " + errorMessage);
            }
        }
    }

    private void redraw() {
        if (currentBoard != null) {
            ChessBoardBuilder boardBuilder = new ChessBoardBuilder(currentBoard, currentGame);
            boardBuilder.printBoard(currentPlayerColor, null);
        } else {
            System.out.println("No game is currently loaded.");
        }
    }

    private void makeMove() {
        try {
            System.out.print("Enter move to execute (e.g., a2-a4): ");
            String moveInput = scanner.nextLine();
            String[] movePositions = moveInput.split("-");
            ChessPosition start = ChessPosition.getPositionFromString(movePositions[0].trim(), currentPlayerColor.equalsIgnoreCase("black"));
            ChessPosition end = ChessPosition.getPositionFromString(movePositions[1].trim(), currentPlayerColor.equalsIgnoreCase("black"));

            if (start != null && end != null) {
                ChessMove move = new ChessMove(start, end, null);
                webSocket.sendCommand(new MakeMove(authData.authToken(), currentGameId, move));
                System.out.println("Move sent to the server.");
            } else {
                System.out.println("Invalid move format.");
            }
        } catch (Exception e) {
            System.out.println("Failed to make a move. Error: " + e.getMessage());
        }
    }

    private void highlightMoves() throws Exception {
        try {
            if(currentBoard != null && currentGame!= null) {
                System.out.println("Enter the position of the piece you want to move: (e.g., a1) ");
                String positionInput = scanner.nextLine();
                ChessPosition piecePosition = new ChessPosition(-1,-1);
                piecePosition = ChessPosition.getPositionFromString(positionInput, currentPlayerColor.toLowerCase(Locale.ROOT).equals("black"));
                ChessBoardBuilder boardBuilder = new ChessBoardBuilder(currentBoard, currentGame);
                boardBuilder.printBoard(currentPlayerColor, piecePosition);
            }
        }
        catch (Exception e) {
            //throw e;
            System.out.println("Unable to highlight valid moves.");
            System.out.println(e.getMessage());
        }
    }

    private void resign() {
        try {
            System.out.print("Are you sure you want to resign? [y/n]: ");
            String confirmation = scanner.nextLine();
            if (confirmation.equalsIgnoreCase("y")) {
                webSocket.sendCommand(new Resign(authData.authToken(), currentGameId));
                System.out.println("Game over. You have resigned.");
            }
        } catch (Exception e) {
            System.out.println("Failed to resign. Error: " + e.getMessage());
        }
    }

    private void leave() {
        try {
            webSocket.sendCommand(new Leave(authData.authToken(), currentGameId));
            state = ClientState.POST_LOGIN;
            System.out.println("You have left the game.");
        } catch (Exception e) {
            System.out.println("Failed to leave the game. Error: " + e.getMessage());
        }
    }

    private void leaveToPostLogin() {
        state = ClientState.POST_LOGIN;
        System.out.println("You have left observing mode.");
    }

    private void logout() {
        try {
            serverFacade.logout(authData.authToken());
            state = ClientState.PRE_LOGIN;
            System.out.println("Logged out successfully.");
        } catch (Exception e) {
            System.out.println("Failed to logout. Error: " + e.getMessage());
        }
    }

    private void quit() {
        System.out.println("Goodbye!");
        System.exit(0);
    }

    // ** Help Commands **
    private void helpPreLogin() {
        System.out.println("register - to create a new account");
        System.out.println("login - to play chess");
        System.out.println("quit - exit the program");
        System.out.println("help - repeat commands");
    }

    private void helpPostLogin() {
        System.out.println("create game - create a new game");
        System.out.println("list games - list all existing games");
        System.out.println("join game - join an existing game");
        System.out.println("observe game - join a game as an observer");
        System.out.println("logout - log out of your account");
        System.out.println("quit - exit the program");
        System.out.println("help - repeat commands");
    }

    private void helpInGame() {
        System.out.println("redraw - redraw the chess board");
        System.out.println("leave - leave the current game");
        System.out.println("make move - move one of your pieces");
        System.out.println("resign - resign and end the game");
        System.out.println("highlight legal moves - display valid moves for a piece");
        System.out.println("help - repeat commands");
    }

    private void helpObserving() {
        System.out.println("redraw - redraw the chess board");
        System.out.println("leave - leave the current game");
        System.out.println("highlight legal moves - display valid moves for a piece");
        System.out.println("help - repeat commands");
    }

    // ** Notification Handlers **
    @Override
    public void notify(Notification notification) {
        System.out.println("INFO: " + notification.message);
    }

    @Override
    public void warn(ErrorMessage errorMessage) {
        System.out.println("ERROR: " + errorMessage.errorMessage);
    }

    @Override
    public void loadGame(LoadGame loadGame) {
        this.currentGame = loadGame.game.game();
        this.currentBoard = loadGame.game.game().getBoard();
        redraw();
    }
}
