package ui.websocket;

import com.google.gson.Gson;
import ui.NotificationHandler;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint{
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) {
        try{
            url = url.replace("http", "ws");
            URI socketURI = new URI(url);
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION:
                            notificationHandler.notify(new Gson().fromJson(message, Notification.class));
                            break;
                        case LOAD_GAME:
                            notificationHandler.loadGame(new Gson().fromJson(message, LoadGame.class));
                            break;
                        case ERROR:
                            notificationHandler.warn(new Gson().fromJson(message, ErrorMessage.class));
                            break;
                        default:
                            notificationHandler.warn(new Gson().fromJson(message, ErrorMessage.class));
                            break;
                    }
                }
            });

        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void sendCommand(UserGameCommand command) throws IOException {
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }
}