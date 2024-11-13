import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/RealEstate";
    private static final String USER = "postgres"; // пользователь б
    private static final String PASSWORD = "admin"; //пароль

    public static Connection getConnection() {
        try {
            // Регистрация драйвера
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Байлансыс сәтті орнатылды!");
            return connection;
        } catch (ClassNotFoundException e) {
            System.out.println("Дарайвер қажет!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}


