package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import consts.Constants;

public class DatabaseUtils {

    private DatabaseUtils() {

    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Constants.DB_HOST, Constants.DB_USER,
                Constants.DB_PASSWORD);

    }
}
