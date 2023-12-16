package com.bridgelabz.service;

import com.bridgelabz.entity.Employee;
import com.bridgelabz.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmployeeServiceDB {

    private PreparedStatement employeeDataStatement;
    private static EmployeeServiceDB single_instance = null;

    private EmployeeServiceDB(){}

    public static synchronized EmployeeServiceDB getInstance()
    {
        if (single_instance == null)
            single_instance = new EmployeeServiceDB();

        return single_instance;
    }



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




    /*
     * Read Data from database in range
     */
    public List<Employee> readDataInRange(String date1, String date2) throws DatabaseException{
        try {
            String sqlQuery = String.format("Select * From employee_payroll Where startDate BETWEEN CAST('%s' AS Date) and CAST('%s' AS Date);", date1,date2);
            List<Employee> employeeDataList = new ArrayList<>();

            Connection connection = this.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sqlQuery);

            employeeDataList = this.getEmployeeDataPopulate(result);
            connection.close();
            return employeeDataList;
        } catch (SQLException e) {
            throw new DatabaseException("Read Data In Range : " + e.getMessage());
        }
    }


    /*
     * Method to find min, max, average, count
     * based on gender
     */
    public HashMap<String, Integer> readDataMinMaxAverage(String gender) throws DatabaseException {
        try {
            String sqlQuery = String.format("Select SUM(salary) as SUM,AVG(salary) as AVG,MAX(salary) as " +
                                            "MAX,MIN(salary) as MIN,COUNT(*) AS COUNT FROM employee_payroll " +
                                            "Where gender='%s' Group By gender;", gender);
            Connection connection = this.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sqlQuery);
            HashMap<String,Integer> data = new HashMap<>();
            while(result.next()){
                data.put("SUM",result.getInt("SUM"));
                data.put("AVG",result.getInt("AVG"));
                data.put("MAX",result.getInt("MAX"));
                data.put("MIN",result.getInt("MIN"));
                data.put("COUNT",result.getInt("COUNT"));
            }
            connection.close();
            return data;
        }catch (SQLException e){
            throw new DatabaseException(e.getMessage());
        }
    }


    public Employee addEmployeeToDB(String name, int salary, LocalDate date, String gender) throws DatabaseException{
        Connection connection = null;
        Employee employee = null;
        try {
            connection = this.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DatabaseException("Error in stable in connection in addEmployee Method: " + e.getMessage());
        }

        try {
            int employeeID = -1;
            String sql = String.format("INSERT INTO employee_payroll (name,salary,startDate,gender) values " +
                                        "('%s','%s','%s','%s');",name,salary,Date.valueOf(date),gender);
            Statement statement = connection.createStatement();
            int rowAffected = statement.executeUpdate(sql,statement.RETURN_GENERATED_KEYS);
            if(rowAffected == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()) employeeID = resultSet.getInt(1);
            }
            statement.close();
            if(insertIntoPayrollDetails(salary,employeeID,connection) == 1){
                employee = new Employee(employeeID,name,salary,date);
            }
        }catch (SQLException e) {
            try {
                connection.rollback();
                return employee;
            } catch (SQLException ex) {
                throw  new DatabaseException("Error in rollback in addEmployeeToDB:  " + e.getMessage());
            }
        }

        try {
            connection.commit();
        } catch (SQLException e) {
            throw  new DatabaseException("Error in commit in addEmployeeToDB:  " + e.getMessage());
        }finally {
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return employee;
    }

    /**
     * Insert data into payroll details table
     *
     * @param salary
     * @param employeeId
     * @param connection
     * @return
     * @throws DatabaseException
     */

    private int insertIntoPayrollDetails(int salary, int employeeId, Connection connection) throws DatabaseException {
        try {

            double deductions = salary * 0.2;
            double taxablepay = salary - deductions;
            double tax = taxablepay * 0.1;
            double netpay = salary - tax;
            String sql = String.format("INSERT INTO payroll_details (employee_id,basic_pay,deductions," +
                            "taxable_pay,tax,net_pay) Values (%s,%s,%s,%s,%s,%s)",employeeId,
                    salary,deductions,taxablepay,tax,netpay);
            Statement statement = connection.createStatement();
            int rowAffected = statement.executeUpdate(sql);
            statement.close();
            return rowAffected;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw  new DatabaseException("Error in rollback in insertIntoPayrollDetails:  " + e.getMessage());

            }
            throw new DatabaseException("Insert INTO Payroll Error: " + e.getMessage());
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

}
