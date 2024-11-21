package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.ConnectionManager;
import websocket.commands.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final SQLUserDAO userDao = new SQLUserDAO();
    private final SQLGameDAO gameDao = new SQLGameDAO();
    private final SQLAuthDAO authDao = new SQLAuthDAO();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ResponseException, Exception {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);

        switch (action.getCommandType()) {
            case CONNECT -> connect(new Gson().fromJson(message, Connect.class), session);

        }

    }

    private void connect(Connect connect, Session session) {
        try {
            AuthData authData = authDao.getAuth(connect.)
            GameData gameData = gameDao.getGame(connect.gameId);

        }
    }


}
