package DataBase;

import org.mariadb.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;

public class MariaDataBaseConnector {
//    private static String URL = "jdbc:mariadb://192.168.0.200/work";
    private static String URL = "jdbc:mariadb://127.0.0.1/work";
//    private static String URL = "jdbc:mysql://localhost/work?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    //    private static String URL = "jdbc:mariadb://ikosmov.hldns.ru/work";
    private static String USER = "root";
    private static String PASSWORD = "root";

    public static Connection getConnection() {
        Connection connection;
        System.out.println("connecting");
        try {
            Driver driver = new Driver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("connected");
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
