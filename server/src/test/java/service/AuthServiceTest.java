package service;

import dataaccess.AuthMemoryDAO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {
    static final AuthService service = new AuthService(new AuthMemoryDAO());

}
