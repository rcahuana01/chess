package dataaccess;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO{
    @Override
    public AuthData createAuth(String username) throws ResponseException {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {

    }

    @Override
    public void clear() throws ResponseException {

    }
}
