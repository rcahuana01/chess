package server.websocket;

import com.google.gson.Gson; // Used for converting Java objects to JSON.
import org.eclipse.jetty.websocket.api.Session; // Represents a WebSocket session.
import websocket.messages.ServerMessage; // Represents messages sent from the server.

import java.util.ArrayList; // Used for storing a list of connections to be removed.
import java.util.concurrent.ConcurrentHashMap; // Thread-safe map for managing connections.

/**
 * Manages WebSocket connections and facilitates broadcasting messages
 * to connected clients while maintaining thread safety.
 */
public class ConnectionManager {

    // A thread-safe map to store active connections keyed by visitor name.
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    /**
     * Adds a new connection to the manager.
     *
     * @param visitorName The name of the visitor associated with this connection.
     * @param session The WebSocket session for the connection.
     * @param gameId The ID of the game this visitor is associated with.
     */
    public void add(String visitorName, Session session, Integer gameId) {
        // Create a new connection object and add it to the map.
        var connection = new Connection(visitorName, session, gameId);
        connections.put(visitorName, connection);
    }

    /**
     * Removes a connection from the manager.
     *
     * @param visitorName The name of the visitor whose connection should be removed.
     */
    public void remove(String visitorName) {
        // Remove the connection from the map based on the visitor name.
        connections.remove(visitorName);
    }

    /**
     * Broadcasts a message to all connections associated with a specific game,
     * excluding a specific visitor.
     *
     * @param excludeVisitor The visitor to exclude from the broadcast.
     * @param notification The message to broadcast, as a ServerMessage object.
     * @param gameId The ID of the game whose participants will receive the message.
     * @throws Exception If an error occurs during message sending.
     */
    public void broadcast(String excludeVisitor, ServerMessage notification, Integer gameId) throws Exception {
        // Temporary list to store connections that are no longer active.
        var removeList = new ArrayList<Connection>();

        // Iterate through all connections in the map.
        for (var c : connections.values()) {
            // Check if the connection's session is still open.
            if (c.session.isOpen()) {
                // If the connection matches the game ID and is not excluded, send the message.
                if (!(c.visitorName.equals(excludeVisitor)) && c.gameId == gameId) {
                    // Convert the notification to JSON format and send it to the client.
                    c.send(new Gson().toJson(notification));
                }
            } else {
                // If the session is closed, mark it for removal.
                removeList.add(c);
            }
        }

        // Remove all inactive connections from the map.
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }
}
