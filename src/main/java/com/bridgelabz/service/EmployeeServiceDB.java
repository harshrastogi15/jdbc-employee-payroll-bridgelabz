package com.bridgelabz.service;

import com.bridgelabz.entity.Employee;
import com.bridgelabz.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
            String password = "harsh";
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

    /**
     * Read all the data from database
     *
     * @return
     * @throws DatabaseException
     */
    public List<Employee> readData() throws DatabaseException {
        try {
            String sqlQuery = "SELECT * FROM employee_payroll";
            List<Employee> employeeDataList = new ArrayList<>();

            Connection connection = this.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sqlQuery);

            while(result.next()){
                int id = result.getInt("id");
                String name = result.getString("name");
                int salary = result.getInt("salary");
                LocalDate start = result.getDate("startDate").toLocalDate();
                employeeDataList.add(new Employee(id,name,salary,start));

            }

            connection.close();
            return employeeDataList;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

}
