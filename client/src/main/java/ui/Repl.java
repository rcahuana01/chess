package ui;

import chess.ChessGame;
import dataaccess.DataAccessException;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static websocket.messages.ServerMessage.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;

public class Repl implements ui.websocket.NotificationHandler {
    private final ChessClient client;

    public Repl(String serverUrl) throws DataAccessException {
        client = new ChessClient(serverUrl, this);
    }
    public void run() {
        System.out.println("Welcome to the chess game. Type help to get started.");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){

            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (DataAccessException e) {
                System.out.println("Server error");
            } catch (Exception e) {
                System.out.println("Invalid input");
            }

        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(ServerMessage message) {
        System.out.println("Notification: " + message.getMessage());
    }

}
