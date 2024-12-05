package websocket.commands;

public class Leave extends UserGameCommand{

    public Leave(String authToken, Integer gameId){
        super(CommandType.LEAVE, authToken, gameId);
    }
}
