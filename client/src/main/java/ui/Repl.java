package ui;

import java.util.Scanner;
import ui.websocket.NotificationHandler;

import static ui.EscapeSequences.*;

public class Repl{
    private final ChessClient client;

    public Repl(String serverUrl) implements NotificationHandler {
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

    public void notify(Notification notification) {
        System.out.println(RED + notification.message());
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}
