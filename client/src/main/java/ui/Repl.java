package ui;

import chess.ChessGame;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static websocket.messages.ServerMessage.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;

public class Repl implements ui.websocket.NotificationHandler {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
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
            } catch (Throwable e){
                System.out.print("Invalid input.");
            }
        }
        System.out.println();
    }



    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()){
            case LOAD_GAME:
                System.out.println(SET_TEXT_COLOR_BLUE + "Game loaded!");

                Graphics.drawBoard(System.out, new ChessGame(), false);
            case ERROR:
                System.out.println(SET_TEXT_COLOR_RED + "Error from server: " + message.getServerMessageType());

            case NOTIFICATION:
                System.out.println(SET_TEXT_COLOR_YELLOW + "Server notification: " + message.getServerMessageType());
        }
    }
}
