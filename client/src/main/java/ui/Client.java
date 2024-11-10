package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.NotificationHandler;
import model.AuthData;
import model.UserData;

import java.util.Scanner;

import static sun.java2d.windows.GDIBlitLoops.register;

public class Client implements NotificationHandler {

    private ClientState state = ClientState.PRE_LOGIN;
    private final ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

    private Scanner scanner = new Scanner(System.in);
    private AuthData authData = null;
    private int currentGameId = -1;
    private String currentPlayerColor = "";
    private ChessBoard currentBoard = null;
    private ChessGame currentGame = null;

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
                        case ""
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


    @Override
    public HandlerResult handleNotification(Notification notification, Object attachment) {
        return null;
    }
}

    private void displayPreloginCommands() {
    }

