package dataaccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDAO {
    void createUser(UserData userData) throws SQLException, DataAccessException;
    UserData getUser(String username) throws SQLException, DataAccessException;;
    void clear() throws SQLException, DataAccessException;
    }
