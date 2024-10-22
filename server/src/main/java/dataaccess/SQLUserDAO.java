package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLUserDAO implements UserDAO {
    private final List<UserData> userDataList;

    public SQLUserDAO() {
        this.userDataList = new ArrayList<>();
    }

    @Override
    public void createUser(UserData newUser) throws ResponseException {
        if (newUser == null || newUser.username() == null || newUser.username().isEmpty() || newUser.password() == null) {
            throw new ResponseException(500, "Error: Bad request");
        }

        // Check if the user already exists
        for (UserData user : userDataList) {
            if (user.username().equals(newUser.username())) {
                throw new ResponseException(409, "Error: User already exists"); // User conflict
            }
        }

        UserData createdUser = new UserData(newUser.username(), newUser.password(), newUser.email());

        userDataList.add(createdUser);
    }

    @Override
    public UserData getUser(String username) throws ResponseException {
        for (UserData user : userDataList) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void clear() throws ResponseException {
        userDataList.clear();
    }
}
