import org.junit.jupiter.api.BeforeEach;

import model.UserData;

public class SQLUserDAOTest {
    private SQLUserDAO userDAO = new SQLUserDAO();

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO.clear();
    }
    @AfterEach
    void tearDown() throws  DataAccessException {
        userDAO.clear();
    }



}