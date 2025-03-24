package client;

import com.sun.nio.sctp.NotificationHandler;
import exception.ResponseException;
import java.util.Arrays;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private String serverUrl;
    private final NotificationHandler notificationHandler;
    private client.State state = client.State.SIGNEDOUT;
    public ChessClient(String serverUrl, NotificationHandler notificationHandler){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public String eval(String input){
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd){
                case "signin" -> signIn(params);
                default -> help();
            };
        } catch (ResponseException ex){
            return ex.getMessage();
        }
    }
    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - signIn <yourname>
                    - quit
                    """;
        }
        return """
                - list
                - adopt <pet id>
                - rescue <name> <CAT|DOG|FROG|FISH>
                - adoptAll
                - signOut
                - quit
                """;
    }
}
