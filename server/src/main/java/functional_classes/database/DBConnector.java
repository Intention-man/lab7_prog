package functional_classes.database;

import exceptions.DatabaseException;

import java.sql.*;
import java.util.Properties;

public class DBConnector {
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private final String url;
    private final String user;
    private final String password;
    Connection connection;
//    Statement stat;
//    DBCollectionHandler dbCollectionHandler;
//    DBUserHandler dbUserHandler;

    public DBConnector(Properties properties){
        this.url = properties.getProperty("url");
        this.user = properties.getProperty("user");
        this.password = properties.getProperty("password");
        System.out.println(url + user + password);
    }

    public synchronized void createConnection() throws DatabaseException {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(url, user, password);
        }
        catch (SQLException exception) {
            throw new DatabaseException("error during connection to database" + exception.getMessage());
        }
        catch (ClassNotFoundException exception) {
            throw new DatabaseException("data driver not found" + exception.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public synchronized void close() throws SQLException {
        connection.close();
    }
}