package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.NotificationHandler;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Client implements NotificationHandler {

    private ClientState state = ClientState.PRE_LOGIN;
    private final ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

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

    public void run() throws Exception{
        System.out.println("Welcome to Chess!");
        System.out.println();
        do {
            System.out.println("Available commands: ");

            if(state == ClientState.PRE_LOGIN) {
                displayPreloginCommands();

                switch(scanner.nextLine()) {
                    case "1":
                    case "register":
                        register();
                        break;
                    case "2":
                    case "login":
                        login();
                        break;
                    case "3":
                    case "quit":
                        quit();
                        break;
                    case "4":
                    case "help":
                        helpPrelogin();
                        break;
                    default:
                        System.out.println("Invalid command. Please enter: register, login, quit, help");
                        break;
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
        } catch (Exception e) {
            System.out.println("Unable to register.");
            System.out.println(e.getMessage());
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
                System.out.println("You have logged in sucessfully.");
                state = ClientState.PRE_LOGIN;
            }
            catch (Exception e) {
                System.out.println("Unable to login with the information provided.");
                System.out.println(e.getMessage());
            }
        }

        private void quit() {
            System.out.println("See you soon!");
            System.exit(0);
        }

        private void createGame() throws Exception {
            try {
                System.out.println("Enter the name of the game: ");
                String gameName = scanner.nextLine();

                GameData gameData = new GameData(0, null, null, gameName, null);
                serverFacade.createGame(authData.authToken(), gameData);
                System.out.println("Game created successfully!");
            } catch (Exception e) {
                System.out.println("Unable to create game with the information provided");
                System.out.println(" ");
            }
        }

        private void listGames() throw Exception {
            try {
                var games = serverFacade.listGames(authData.authToken());
                int gameIDIndex = 1;
                if (games != null && !(games.games().isEmpty())){
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
            } catch (Exception e) {
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
                    for (GameData game : games.games().isEmpty()) {
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
                } else {
                    serverFacade.joinGame(authData.authToken(), currentGameId, "observer");
                    currentPlayerColor = "";
                    state = ClientState.OBSERVING;
                }
                System.out.println("Joined the game succesfully.");
            } catch (Exception e) {
                System.out.println("Unable to join with the information provided.");
                System.out.println(e.getMessage());
            }
        }

        private void logout() throws Exception {
            serverFacade.logout(authData.authToken());
            state = ClientState.PRE_LOGIN;
        }

        private void redraw() {
            if (currentBoard != null) {
                ChessBoardBuilder boardBuilder = new ChessBoardBuilder((currentBoard, currentGame));
                boardBuilder.printBoard(currentPlayerColor, null);
            }
        }

        private void makeMove() throws Exception {
            try {
                System.out.print(" ");

            }
        }
    @Override
    public HandlerResult handleNotification(Notification notification, Object attachment) {
        return null;
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

            UserData userData = new = UserData(username, password, email)
        }
    }
    }

    private void displayPreloginCommands() {
    }

