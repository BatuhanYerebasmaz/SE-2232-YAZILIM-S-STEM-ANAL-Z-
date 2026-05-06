package moviecriticssystem;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author yereb
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {
public static Connection connect() {
Connection conn = null;
String url = "jdbc:mysql://localhost:3306/MovieCritics";
String user = "root";
String password = "123456";
try {
conn = DriverManager.getConnection(url, user, password);
//JOptionPane.showMessageDialog(null, "Connection Successful!" );
} catch (SQLException e) {
JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
}
return conn;
}
public static void main(String[] args)
{
connect();
}
}
