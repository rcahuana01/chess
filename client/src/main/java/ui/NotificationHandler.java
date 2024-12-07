package ui;

import websocket.messages.*;
import websocket.messages.ErrorMessage;

public interface NotificationHandler {
    void notify(Notification notification);
    void warn(ErrorMessage errorMessage);
    void loadGame(LoadGame loadGame);

}
