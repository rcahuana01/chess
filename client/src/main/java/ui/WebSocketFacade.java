package ui;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
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

    public WebSocketFacade(String url, NotificationHandler notificationHandler){
        try{
            url = url.replace("http", "ws");
            URI socketURI = new URI(url);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler() {
                @Override
                public void onMessage(String message){
                    ServerMessage serverMessage = new Gson().fromJson(message,ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION -> new Gson().fromJson(message, Notification.class);
                        case LOAD_GAME -> notificationHandler.loadGame(new Gson().fromJson(message, LoadGame.class));
                        case ERROR -> notificationHandler.warn(new Gson().fromJson(message, Error.class));
                        default -> notificationHandler.warn(new Gson().fromJson(message, Error.class));
                    }
                }
            });
        } catch (URISyntaxException | IOException | DeploymentException) {
            throw new RuntimeException();
        }
    }

    public void onOpen(Session session, EndpointConfig endpointConfig){

    }

    public void sendCommand(UserGameCommand command) throws IOException{
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }
}
