package websocket.messages;

import model.GameData;

public class LoadGame extends ServerMessage{
    public GameData game;

    public LoadGame(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
