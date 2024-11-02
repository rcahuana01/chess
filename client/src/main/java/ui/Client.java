package ui;

import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.NotificationHandler;

public class Client implements NotificationHandler {

    public void run() throws Exception{
        System.out.println("Welcome to Chess!");
        System.out.println();
    }

    @Override
    public HandlerResult handleNotification(Notification notification, Object attachment) {
        return null;
    }
}

