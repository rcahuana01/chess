package ui;

import websocket.messages.*;
import websocket.messages.Error;

public interface NotificationHandler {
    void notify(Notification notification);
    void warn(Error errorMessage);
    void loadGame(LoadGame loadGame);

}
