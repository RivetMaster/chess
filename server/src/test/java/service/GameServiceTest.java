package service;


import dataaccess.AuthMemoryDAO;
import dataaccess.GameDAO;
import dataaccess.GameMemoryDAO;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    static final GameService service = new GameService(new GameMemoryDAO(), new AuthMemoryDAO());
}
