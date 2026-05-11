package moviecriticssystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    
    public static Connection connect() {
        Connection conn = null;
        String url = "jdbc:mysql://localhost:3306/MovieCritics";
        String user = "root";
        String password = "0874";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
        return conn;
    }
    public static void main(String[] args)
        {
        connect();
    }
}
