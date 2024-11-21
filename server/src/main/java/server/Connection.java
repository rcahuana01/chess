package server;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    public String visitorName;
    public Session session;
    public int gameId;
    public Connection(String visitorName, Session session, int gameId){
        this.visitorName = visitorName;
        this.session = session;
        this.gameId = gameId;
    }

    public void send(String msg) throws Exception {
        session.getRemote().sendString(msg);
    }

    public void sendError(String msg) throws Exception {
        sendError(session.getRemote(), msg);
    }

    public static void sendError(RemoteEndpoint endpoint, String msg) throws Exception{
        var errorMsg = (new Error(String.format("ERROR: %s", msg))).toString();
        System.out.println(errorMsg);
        endpoint.sendString(errorMsg);
    }
}
