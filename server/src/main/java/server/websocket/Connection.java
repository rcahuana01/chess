package server.websocket;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

/**
 * Represents a connection between a WebSocket client and the server.
 * Stores the visitor's information and provides methods for sending messages.
 */
public class Connection {
    // The name of the visitor associated with this connection.
    public String visitorName;

    // The WebSocket session associated with this connection.
    public Session session;

    // The ID of the game the visitor is associated with.
    public int gameId;

    /**
     * Constructs a new Connection object.
     *
     * @param visitorName The visitor's name.
     * @param session The WebSocket session for the visitor.
     * @param gameId The ID of the game the visitor is associated with.
     */
    public Connection(String visitorName, Session session, int gameId) {
        this.visitorName = visitorName;
        this.session = session;
        this.gameId = gameId;
    }

    /**
     * Sends a message to the client connected via this connection.
     *
     * @param msg The message to send as a String.
     * @throws Exception If an error occurs while sending the message.
     */
    public void send(String msg) throws Exception {
        session.getRemote().sendString(msg);
    }

    /**
     * Sends an error message to the client connected via this connection.
     *
     * @param msg The error message to send.
     * @throws Exception If an error occurs while sending the error message.
     */
    private void sendError(String msg) throws Exception {
        sendError(session.getRemote(), msg);
    }

    /**
     * Sends an error message to the specified remote endpoint.
     *
     * @param endpoint The remote endpoint to send the error message to.
     * @param msg The error message to send.
     * @throws Exception If an error occurs while sending the error message.
     */
    private static void sendError(RemoteEndpoint endpoint, String msg) throws Exception {
        // Format the error message and print it to the console.
        var errorMsg = (new Error(String.format("ERROR: %s", msg))).toString();
        System.out.println(errorMsg);

        // Send the error message to the client.
        endpoint.sendString(errorMsg);
    }
}
