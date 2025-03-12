package dataaccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDAO {
    void createUser(UserData userData) throws SQLException, DataAccessException;
    UserData getUser(String username) throws SQLException, DataAccessException;;
    void clear() throws SQLException, DataAccessException;
    String readHashedPasswordFromDatabase(String username) throws DataAccessException;
    void writeHashedPasswordToDatabase (String username, String hashedPassword) throws SQLException, DataAccessException;
    }
