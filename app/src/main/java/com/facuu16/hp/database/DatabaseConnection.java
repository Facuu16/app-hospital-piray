package com.facuu16.hp.database;


import java.sql.*;

public class DatabaseConnection {

    private final Connection connection;

    public DatabaseConnection(String host, String port, String database, String user, String password) {
        try {
            synchronized (this) {
                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
            }
        } catch (NullPointerException | SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

}
