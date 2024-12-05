//package ui;
//
//import com.google.gson.Gson;
//import websocket.messages.Notification;
//import websocket.messages.ServerMessage;
//
//import javax.websocket.*;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//public class WebSocketFacade {
//    Session session;
//
//    public WebSocketFacade(String url){
//        try{
//            url = url.replace("http", "ws");
//            URI socketURI = new URI(url);
//            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//            this.session = container.connectToServer(this, socketURI);
//
//            this.session.addMessageHandler(new MessageHandler() {
//
//                public void onMessage(String message){
//                    ServerMessage serverMessage = new Gson().fromJson(message,ServerMessage.class);
//                    switch (serverMessage.getServerMessageType()) {
//                        case NOTIFICATION -> new Gson().fromJson(message, Notification.class);
//
//                    }
//                }
//            });
//        } catch (URISyntaxException | IOException | DeploymentException) {
//            throw new RuntimeException();
//        }
//    }
//}
