package server;

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

    public void
}
