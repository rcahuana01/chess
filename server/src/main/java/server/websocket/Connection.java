package server.websocket;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String visitorName;
    public Session session;
    public int gameId;

    public Connection(String visitorName, Session session, int gameId) {
        this.visitorName = visitorName;
        this.session = session;
        this.gameId = gameId;

    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    private void sendError(String msg) throws Exception {
        sendError(session.getRemote(), msg);
    }

    private static void sendError(RemoteEndpoint endpoint, String msg) throws Exception {
        var errMsg = (new Error(String.format("ERROR: %s", msg))).toString();
        System.out.println(errMsg);
        endpoint.sendString(errMsg);
    }
}