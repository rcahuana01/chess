package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements  GameDAO{
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public void clear(){
        games.clear();
    }

    void getGame(){

    }

    void listGames(){

    }

    void updateGame(int gameID){

    }

}
