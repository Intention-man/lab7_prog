package functional_classes.database;

import exceptions.DatabaseException;

import java.sql.*;
import java.util.Properties;

public class DBWorker {
    private static final String JDBC_DRIVER = "oracle.jdbc.OracleDriver";
    private final String url;
    private final String user;
    private final String password;
    Connection connection;

    public DBWorker(Properties properties){
        this.url = properties.getProperty("url");
        this.user = properties.getProperty("user");
        this.password = properties.getProperty("password");
    }

    public void createConnection() throws DatabaseException {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(url, user, password);
        }
        catch (SQLException exception) {
            System.out.println(exception);
            throw new DatabaseException("error during connection to database");
        }
        catch (ClassNotFoundException exception) {
//            throw new DatabaseException("data driver not found");
            System.out.println(exception);
        }
    }

    public void executeQuery() throws SQLException {
        Statement stat = connection.createStatement();
        ResultSet res = stat.executeQuery("SELECT * from cars");
        System.out.println(res);
        while (res.next()) {
            // получение и обработка данных
        }
        res.close();
        stat.close();
        connection.close();
    }
}