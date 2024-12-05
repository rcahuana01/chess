package websocket.commands;

public class Resign extends UserGameCommand{

    public Resign(String authToken, Integer gameId){
        super(CommandType.RESIGN, authToken, gameId);
    }
}
