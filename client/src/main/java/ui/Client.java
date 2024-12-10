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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;


public class Client implements NotificationHandler {
    private ClientState state = ClientState.PRE_LOGIN;
    private ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    private WebSocketFacade webSocket = new WebSocketFacade("ws://localhost:8080/ws", this);
    private Scanner scanner = new Scanner(System.in);

    private AuthData authData = null;
    private int currentGameId = -1;
    private String currentPlayerColor = "";
    private ChessBoard currentBoard = null;
    private ChessGame currentGame = null;

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
        System.out.println("1. \"redraw\"");
        System.out.println("2. \"leave\"");
        System.out.println("3. \"make move\"");
        System.out.println("4. \"resign\"");
        System.out.println("5. \"highlight legal moves\"");
        System.out.println("6. \"help\"");
    }

    public void displayObservingCommands(){
        System.out.println("1. \"redraw\"");
        System.out.println("2. \"leave\"");
        System.out.println("3. \"highlight legal moves\"");
        System.out.println("4. \"help\"");
    }

    public void run() throws Exception {
        System.out.println("Welcome to 240 Chess!\n");

        while (true) {
            System.out.println("Available commands: ");

            switch (state) {
                case PRE_LOGIN -> {
                    displayPreloginCommands();
                    switch (scanner.nextLine()) {
                        case "1", "register" -> register();
                        case "2", "login" -> login();
                        case "3", "quit" -> quit();
                        case "4", "help" -> helpPrelogin();
                        default -> System.out.println("Invalid command, please enter: register, login, quit, help");
                    }
                }
                case POST_LOGIN -> {
                    displayPostloginCommands();
                    switch (scanner.nextLine()) {
                        case "1", "create game" -> createGame();
                        case "2", "list games" -> listGames();
                        case "3", "join game" -> joinGame(false);
                        case "4", "observe game" -> joinGame(true);
                        case "5", "logout" -> logout();
                        case "6", "quit" -> quit();
                        case "7", "help" -> helpPostlogin();
                        default -> System.out.println("Invalid command, please enter: create game, list" +
                                " games, join game, observe game, logout, quit, help");
                    }
                }
                case IN_GAME -> {
                    displayIngameCommands();
                    switch (scanner.nextLine()) {
                        case "1", "redraw" -> redraw();
                        case "2", "leave" -> state = ClientState.POST_LOGIN;
                        case "3", "make move" -> makeMove();
                        case "4", "resign" -> resign();
                        case "5", "highlight legal moves" -> highlightMoves();
                        case "6", "help" -> helpIngame();
                        default -> System.out.println("Invalid command, please enter: redraw, leave, " +
                                "make move, resign, highlight legal moves, help");
                    }
                }
                case OBSERVING -> {
                    displayObservingCommands();
                    switch (scanner.nextLine()) {
                        case "1", "redraw" -> redraw();
                        case "2", "leave" -> leave();
                        case "3", "highlight legal moves" -> highlightMoves();
                        case "4", "help" -> helpObserving();
                        default -> System.out.println("Invalid command, please enter: redraw, leave, highlight " +
                                "legal moves, help");
                    }
                }
            }
        }
    }

    private void register() throws Exception {
        try {
            System.out.println("Enter your username: ");
            String username = scanner.nextLine();
            System.out.println("Enter your password: ");
            String password = scanner.nextLine();
            System.out.println("Enter your email: ");
            String email = scanner.nextLine();

            UserData userData = new UserData(username, password, email);
            authData = serverFacade.register(userData);
            System.out.println("You have registered your account and logged in successfully.");

            state = ClientState.POST_LOGIN;
        }
        catch (Exception e) {
            //throw e;
            System.out.println("Unable to register with the information provided." + e.getMessage());
        }
    }

    private void login() throws Exception {
        try {
            System.out.println("Enter your username: ");
            String username = scanner.nextLine();
            System.out.println("Enter your password: ");
            String password = scanner.nextLine();

            UserData userData = new UserData(username, password, null);
            authData = serverFacade.login(userData);
            System.out.println("You have logged in successfully.");

            state = ClientState.POST_LOGIN;
        }
        catch (Exception e) {
            //throw e;
            System.out.println("Unable to login with the information provided.");
        }
    }

    private void quit() {
        System.out.println("Goodbye!");
        System.exit(0);
    }

    private void createGame() throws Exception {
        try {
            System.out.println("Enter the name of the game: ");
            String gameName = scanner.nextLine();

            GameData gameData = new GameData(0, null, null, gameName, null);
            serverFacade.createGame(authData.authToken(), gameData);
            System.out.println("Game created successfully!");
        }
        catch (Exception e) {
            //throw e;
            System.out.println("Unable to create game with the information provided");
            System.out.println(" ");
        }
    }

    private void listGames() throws Exception {
        try {
            var games = serverFacade.listGames(authData.authToken());
            int gameIDIndex = 1;
            if (games != null && !(games.games().isEmpty())) {
                System.out.println("List of current games:");
                for (GameData game : games.games()) {
                    System.out.println("Game ID: " + gameIDIndex);
                    System.out.println("Game Name: " + game.gameName());
                    System.out.println("White Player: " + game.whiteUsername());
                    System.out.println("Black Player: " + game.blackUsername());
                    System.out.println(" ");
                    gameIDIndex++;
                }
            } else {
                System.out.println("No games available.");
            }
        }
        catch (Exception e) {
            //throw e;
            System.out.println("Unable to list games.");
            System.out.println(e.getMessage());
        }
    }

    private void joinGame(boolean observer) throws Exception {
        try {
            var games = serverFacade.listGames(authData.authToken());
            Map<Integer, Integer> gamesIDs = new HashMap<>();
            int gameIDIndex = 1;
            if (games != null && !(games.games().isEmpty())) {
                for (GameData game : games.games()) {
                    gamesIDs.put(gameIDIndex, game.gameID());
                    gameIDIndex++;
                }
            }

            System.out.println("Enter the Game ID:");
            int gameId = scanner.nextInt();
            scanner.nextLine();
            currentGameId = gamesIDs.get(gameId);

            if (!observer) {
                System.out.println("Enter player color (WHITE/BLACK):");
                String playerColor = scanner.nextLine().toUpperCase();
                serverFacade.joinGame(authData.authToken(), currentGameId, playerColor);
                currentPlayerColor = playerColor;
                state = ClientState.IN_GAME;
            }
            else {
                serverFacade.joinGame(authData.authToken(), currentGameId, "observer");
                currentPlayerColor = "";
                state = ClientState.OBSERVING;
            }

            webSocket.sendCommand(new Connect(authData.authToken(), currentGameId, currentPlayerColor.
                    equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE :
                    ChessGame.TeamColor.BLACK, observer));

            System.out.println("Joined game successfully.");
        }
        catch (Exception e) {
            //throw e;
            System.out.println("Unable to join game with the information provided. Username already taken.");
        }
    }

    private void logout() throws Exception {
        serverFacade.logout(authData.authToken());
        state = ClientState.PRE_LOGIN;
    }

    private void redraw() {
        if (currentBoard != null) {
            ChessBoardBuilder boardBuilder = new ChessBoardBuilder(currentBoard, currentGame);
            boolean reversed = currentPlayerColor.equals("BLACK");
            boardBuilder.printBoard(currentPlayerColor, null);
        }
    }


    private void makeMove() throws Exception {
        try {
            System.out.print("Enter move to execute (e.g., a1-a5): ");
            String moveInput = scanner.nextLine();
            String[] movePositions = moveInput.split("-");
            ChessPosition start = ChessPosition.getPositionFromString(movePositions[0].trim().toLowerCase(),
                    currentPlayerColor.equalsIgnoreCase("BLACK"));
            ChessPosition end = ChessPosition.getPositionFromString(movePositions[1].trim().toLowerCase(),
                    currentPlayerColor.equalsIgnoreCase("BLACK"));

            if (start == null || end == null) {
                System.out.println("Invalid move format. Use notation like a1-a5.");
                return;
            }

            ChessMove move = new ChessMove(start, end, null);

            // Handle promotion input
            if (currentGame != null && currentGame.validMoves(start).stream()
                    .anyMatch(validMove -> validMove.getEndPosition().equals(end) && end.getRow() ==
                            (currentPlayerColor.equals("WHITE") ? 8 : 1))) {
                System.out.print("Promotion! Choose a piece (QUEEN, ROOK, BISHOP, KNIGHT): ");
                String promotionPiece = scanner.nextLine().toUpperCase();
                move = new ChessMove(start, end, ChessPiece.PieceType.valueOf(promotionPiece));
            }

            // Send move to server
            webSocket.sendCommand(new MakeMove(authData.authToken(), currentGameId, move));
        } catch (Exception e) {
            System.out.println("Error making move: " + e.getMessage());
        }
    }



    private void resign() throws Exception {
        try {
            System.out.print("Are you sure you want to resign? [y/n]: ");
            String confirmation = scanner.nextLine();
            if (confirmation.equals("y")) {
                webSocket.sendCommand(new Resign(authData.authToken(), currentGameId));
                System.out.println("Game is over!");
            }
        }
        catch (Exception e) {
            //throw e;
            System.out.println("Unable to resign the game");
            System.out.println(" ");
        }
    }

    private void leave() throws Exception {
        try {
            webSocket.sendCommand(new Leave(authData.authToken(), currentGameId));
            state = ClientState.POST_LOGIN;
        }
        catch (Exception e) {
            //throw e;
            System.out.println("Unable to leave the game.");
            System.out.println(e.getMessage());
        }
    }

    private void highlightMoves() throws Exception {
        try {
            if(currentBoard != null && currentGame!= null) {
                System.out.println("Enter the position of the piece you want to move: (e.g., a1) ");
                String positionInput = scanner.nextLine();
                ChessPosition piecePosition = new ChessPosition(-1,-1);
                piecePosition = piecePosition.getPositionFromString(positionInput, currentPlayerColor.
                        toLowerCase(Locale.ROOT).equals("black"));
                ChessBoardBuilder boardBuilder = new ChessBoardBuilder(currentBoard, currentGame);
                boardBuilder.printBoard(currentPlayerColor, piecePosition);
            }
        }
        catch (Exception e) {
            //throw e;
            System.out.println("Unable to highlight valid moves.");
        }
    }

    private void helpPrelogin() {
        System.out.println("register - to create a new account");
        System.out.println("login - to play chess");
        System.out.println("quit -  exit the program");
        System.out.println("help - repeat commands");
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

    private void helpIngame() {
        System.out.println("redraw - redraw the chess board");
        System.out.println("leave - leave the current game");
        System.out.println("make move - move one of your pieces");
        System.out.println("resign - leave and end the game");
        System.out.println("highlight legal moves - redraw board with valid moves for a piece");
        System.out.println("help - repeat commands");
    }

    private void helpObserving() {
        System.out.println("redraw - redraw the chess board");
        System.out.println("leave - leave the current game");
        System.out.println("highlight legal moves - redraw board with valid moves for a piece");
        System.out.println("help - repeat commands");
    }

    @Override
    public void notify(Notification notification) {
        System.out.println("INFO: " + notification.message);
    }

    @Override
    public void warn(ErrorMessage error) {
        System.out.println("ERROR: " + error.errorMessage);
    }

    @Override
    public void loadGame(LoadGame loadGame) {
        this.currentGame = loadGame.game.game();
        this.currentBoard = loadGame.game.game().getBoard();
        redraw();
    }
}