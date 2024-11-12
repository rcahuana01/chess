package ui;

import chess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import ui.ChessBoardBuilder;
import ui.ClientState;
import ui.ServerFacade;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {

    private final ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    private ClientState state = ClientState.PRE_LOGIN;
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
            default -> System.out.println("Invalid command, please enter: create game, list games, join game, observe game, logout, quit, help");
        }
    }

    private void handleIngameCommands() {
        displayIngameCommands();
        switch (scanner.nextLine().trim().toLowerCase()) {
            case "1", "redraw" -> redraw();
            case "2", "leave" -> state = ClientState.POST_LOGIN;
            case "3", "make move" -> System.out.println("Feature to be implemented in gameplay phase.");
            case "4", "resign" -> state = ClientState.POST_LOGIN;
            case "5", "highlight legal moves" -> System.out.println("Feature to be implemented.");
            case "6", "help" -> helpPostlogin();
            default -> System.out.println("Invalid command, please enter: redraw, leave, make move, resign, highlight legal moves, help");
        }
    }

    private void helpPostlogin() {
    }

    private void handleObservingCommands() {
        displayObservingCommands();
        switch(scanner.nextLine().trim().toLowerCase()) {
            case "1", "redraw" -> redraw();
            case "2", "leave" -> state = ClientState.POST_LOGIN;
            case "3", "highlight legal moves" -> System.out.println("Feature to be implemented.");
            case "4", "help" -> helpPostlogin();
            default -> System.out.println("Invalid command, please enter: redraw, leave, highlight legal moves, help");
        }
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
                    System.out.printf("Game ID: %d, Name: %s, White Player: %s, Black Player: %s%n", gameIDIndex++, game.gameName(), game.whiteUsername(), game.blackUsername());
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

            System.out.println("Joined game successfully.");

        }
        catch (Exception e) {
            //throw e;
            System.out.println("Unable to join game with the information provided.");
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

    private void redraw() {
        if (currentBoard != null) {
            System.out.println("Redrawing the chess board...");
            new ChessBoardBuilder(currentBoard, currentGame).printBoard(currentPlayerColor, null);
        } else {
            System.out.println("Current board is null. Unable to redraw.");
        }
    }

    private void quit() {
        System.out.println("See you soon!");
        System.exit(0);
    }
}
