package model;

import java.util.Objects;

public record JoinRequest(int gameId, String playerColor) {
    public String getPlayerColor(String username) {
        if (Objects.equals(username, playerColor)) {
            return "WHITE";
        } else {
            return "BLACK";
        }
    }
}
