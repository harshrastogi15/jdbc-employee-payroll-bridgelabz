package com.bridgelabz.service;

import com.bridgelabz.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EmployeeServiceDB {

    /**
     * Method to establish connection with mysql
     *
     * @return
     * @throws DatabaseException
     */
    private Connection getConnection() throws DatabaseException {

        try {
            System.out.println("Connecting to Database");
            String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false&allowPublicKeyRetrieval=true";
            String user = "root";
            String password = "";
            Connection connection;
            connection = DriverManager.getConnection(jdbcURL,user,password);
            System.out.println("Connected to Database : " + jdbcURL);
            return connection;
        } catch (SQLException e) {
            throw new DatabaseException("Error in connecting Database: " + e.getMessage());
        }
    }

    public boolean checkConnectionToDB() throws DatabaseException{
        try {
            Connection connection = this.getConnection();
            connection.close();
            return true;
        } catch (SQLException e) {
            throw new DatabaseException("Error in connection : " + e.getMessage());
        }
    }

}
