package service;

import dataaccess.*;
import org.junit.jupiter.params.provider.Arguments;


public class TestMethodClass {

    public static Arguments[] provideClasses(Class<? extends GameDAO>[] gameClasses,
                                                   Class<? extends UserDAO>[] userClasses) {
        if(gameClasses != null) {
            return new Arguments[]{
                    Arguments.of(AuthMemoryDAO.class, gameClasses[0]),
                    Arguments.of(AuthSQLDAO.class, gameClasses[0]),
                    Arguments.of(AuthMemoryDAO.class, gameClasses[1]),
                    Arguments.of(AuthSQLDAO.class, gameClasses[1])
            };
        }
        if(userClasses != null) {
            return new Arguments[]{
                    Arguments.of(AuthMemoryDAO.class, userClasses[0]),
                    Arguments.of(AuthSQLDAO.class, userClasses[0]),
                    Arguments.of(AuthMemoryDAO.class, userClasses[1]),
                    Arguments.of(AuthSQLDAO.class, userClasses[1])
            };
        }
        return null;
    }
}
