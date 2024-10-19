import org.junit.jupiter.api.BeforeEach;

public class SQLGameDAOTest {
    private SQLUserDAO gameDAO = new SQLGameDAO();

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO.clear();
    }


}
