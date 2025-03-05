package dataaccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements  GameDAO{
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public void clear(){
        games.clear();
    }

    public void createGame(int gameId){
        games.put(gameId, new GameData(gameId, null, null, null, null));
    }

    public GameData getGame(int gameId){
        return games.get(gameId);
    }

    public void updateGameList(GameData gameData) {
        games.put(gameData.gameID(), gameData);

    }

    public void updateGamePlayers(int gameId, UserData user, boolean isWhite) {
        GameData game = games.get(gameId);  // Get the game using its ID

        // Create a new updated game based on the current game and assign the player
        GameData updatedGame;

        if (isWhite) {
            // If the player is white, update the white username field
            updatedGame = new GameData(gameId, user.username(), game.blackUsername(), game.gameName(), game.game());
        } else {
            // If the player is black, update the black username field
            updatedGame = new GameData(gameId, game.whiteUsername(), user.username(), game.gameName(), game.game());
        }

        // Put the updated game back into the games map
        games.put(gameId, updatedGame);
    }


    public List<GameData> getAvailableGames(){
        List<GameData> availableGames = new ArrayList<>();
        for (GameData game : games.values()){
            if (game.whiteUsername() == null || game.blackUsername() == null) {
                availableGames.add(game);
            }
        }
        return availableGames;
    }

}
