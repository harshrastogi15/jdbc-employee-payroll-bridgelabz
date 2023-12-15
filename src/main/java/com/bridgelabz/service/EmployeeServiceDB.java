package com.bridgelabz.service;

import com.bridgelabz.entity.Employee;
import com.bridgelabz.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeServiceDB {

    private PreparedStatement employeeDataStatement;

    /**
     * Method to establish connection with mysql
     *
     * @return
     * @throws DatabaseException
     */

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

    /*
     * Method to update Salary
     */
    public int updateSalaryData(String name, int salary) throws DatabaseException {
        return this.firstMethodToUpdateData(name,salary);
    }

    public List<Employee> getEmployeeData(String name) throws DatabaseException{
        List<Employee> employeeDataList = null;
        if(this.employeeDataStatement == null){
            this.prepareStatementForEmployeeData();
        }
        try {
            employeeDataStatement.setString(1,name);
            ResultSet resultSet = employeeDataStatement.executeQuery();
            employeeDataList = this.getEmployeeDataPopulate(resultSet);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
        return employeeDataList;
    }

    private int firstMethodToUpdateData(String name, int salary) throws DatabaseException {
        String sql = String.format("Update employee_payroll set Salary = %d where name='%s';",salary,name);
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

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

    private void prepareStatementForEmployeeData() throws DatabaseException {
        try{
            Connection connection = this.getConnection();
            String sqlQuery = "Select * From employee_payroll where name = ?";
            employeeDataStatement = connection.prepareStatement(sqlQuery);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private List<Employee> getEmployeeDataPopulate(ResultSet result) throws SQLException {
        List<Employee> employeeDataList = new ArrayList<>();
        while(result.next()){
            int id = result.getInt("id");
            String name = result.getString("name");
            int salary = result.getInt("salary");
            LocalDate start = result.getDate("startDate").toLocalDate();
            employeeDataList.add(new Employee(id,name,salary,start));

        }
        return employeeDataList;
    }

}
