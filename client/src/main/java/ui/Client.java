package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.NotificationHandler;
import model.AuthData;
import model.UserData;

import java.util.Scanner;

public class Client implements NotificationHandler {

    private ClientState state = ClientState.PRE_LOGIN;
    private ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

    private Scanner state = new Scanner(System.in);
    private AuthData authData = null;
    private int currentGameId = -1;
    private String currentPlayerColor = "";
    private ChessBoard currentBoard = null;
    private ChessGame currentGame = null;

    public void run() throws Exception{
        System.out.println("Welcome to Chess!");
        System.out.println();
    }

    public void register() throws Exception {
        try {
            System.out.println("Enter your username: ");
            String username = state.nextLine();
            System.out.println("Enter your password: ");
            String password = state.nextLine();
            System.out.println("Enter your email: ");
            String email = state.nextLine();

            UserData userData = new UserData(username, password, email);
            AuthData authData = serverFacade.reg
        }
    }
    @Override
    public HandlerResult handleNotification(Notification notification, Object attachment) {
        return null;
    }
}

