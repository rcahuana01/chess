package client;

import com.sun.nio.sctp.NotificationHandler;

public class ChessClient {
    private String visitorName = null;
    private String serverUrl;
    private final NotificationHandler notificationHandler;
    private client.State state = client.State.SIGNEDOUT;
    public ChessClient(NotificationHandler notificationHandler){

    }
}
