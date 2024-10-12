package dataaccess;

import chess.ChessGame;
import model.UserData;

public interface DataAccess {
    void clear();
    void createUser(UserData user);
    UserData getUser(String username);
    void createGame(ChessGame game);
    ChessGame getGame(String gameID);
    List<ChessGame> listGames();
    void updateGame(String gameID, ChessGame game);
    void createAuth(AuthData auth);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
}
