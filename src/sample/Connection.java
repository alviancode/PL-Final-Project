package sample;

import java.sql.*;

public class Connection {
    private String url = "jdbc:mysql://localhost:3306/pos_system?useSSL=false";
    private String username = "root";
    private String password = "AlvianWijaya";
    public java.sql.Connection connection = null;


    // Class constructor of Connection class
    public Connection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Function that return prepStat
    public PreparedStatement getPrepStat(String query) {
        try {
            PreparedStatement prepStat = connection.prepareStatement(query);
            return prepStat;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // Function that return value of username
    public String getUsername() {
        return username;
    }


    // Function that return value of username
    public String getPassword() {
        return password;
    }

}
