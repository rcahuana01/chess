package client.websocket;

import websocket.commands.UserGameCommand;

public interface NotificationHandler {
    void notify(Notification notification);
}