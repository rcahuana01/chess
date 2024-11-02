package ui;

import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.NotificationHandler;

import java.util.Scanner;

public class Client implements NotificationHandler {


    private Scanner state = new Scanner(System.in);

    public void run() throws Exception{
        System.out.println("Welcome to Chess!");
        System.out.println();
    }

    public void register() throws Exception {
        try {
            System.out.println("Enter your username: ");
            String username = state.nextLine();
        }
    }
    @Override
    public HandlerResult handleNotification(Notification notification, Object attachment) {
        return null;
    }
}

