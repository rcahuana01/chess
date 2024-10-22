package dataaccess;

import model.UserData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLUserDAO implements UserDAO {
    private final List<UserData> userDataList; // In-memory storage for user data

    public SQLUserDAO() {
        this.userDataList = new ArrayList<>(); // Initialize the list
    }

    @Override
    public void createUser(UserData newUser) throws ResponseException {
        if (newUser == null || newUser.username() == null || newUser.username().isEmpty()) {
            throw new ResponseException(400, "Error: Bad request"); // Validate user data
        }

        // Check if the user already exists
        for (UserData user : userDataList) {
            if (user.username().equals(newUser.username())) {
                throw new ResponseException(409, "Error: User already exists"); // User conflict
            }
        }

        // Create a new UserData instance from the fields of newUser
        UserData createdUser = new UserData(newUser.username(), newUser.email(), newUser.password()); // Adjust field names based on your UserData structure

        // Add the new user to the in-memory list
        userDataList.add(createdUser);
    }

    @Override
    public UserData getUser(String username) throws ResponseException {
        for (UserData user : userDataList) {
            if (user.username().equals(username)) {
                return user; // Return the user if found
            }
        }
        throw new ResponseException(404, "Error: User not found"); // User not found
    }

    @Override
    public void clear() throws ResponseException {
        userDataList.clear(); // Clear the in-memory list of users
    }

    // Additional method to list all users (optional)
    public Collection<UserData> listUsers() throws ResponseException {
        return new ArrayList<>(userDataList); // Return a copy of the user list
    }
}
