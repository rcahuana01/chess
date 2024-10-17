package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.UserData;

import java.util.List;

public class MemoryUserDAO implements UserDAO{
    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createGame(ChessGame game) {

    }

    @Override
    public ChessGame getGame(String gameID) {
        return null;
    }

    @Override
    public List<ChessGame> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(String gameID, ChessGame game) {

    }

    @Override
    public void createAuth(AuthData auth) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }
}
