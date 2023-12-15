package com.bridgelabz.service;

import com.bridgelabz.entity.Employee;
import com.bridgelabz.exception.DatabaseException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class EmployeePayrollService {

    public enum IOService{CONSOLE_IO, FILE_IO, DB_IO, REST_IO;}
    EmployeeServiceDB employeeServiceDb;

    List<Employee> employeeDataList;
    public EmployeePayrollService(){
        employeeServiceDb = EmployeeServiceDB.getInstance();
    }
    public List<Employee> readFromDataBase(IOService ioService) throws DatabaseException{

        if(ioService.equals(IOService.DB_IO)){
            this.employeeDataList = employeeServiceDb.readData();
        }
        return this.employeeDataList;
    }

    public void updateSalary(String name, int salary) throws DatabaseException {
        int result = employeeServiceDb.updateSalaryData(name,salary);
        if(result == 0){
            return ;
        }
        Employee employeeData = this.getEmployeePayrollData(name);
        if(employeeData != null) employeeData.salary = salary;
    }

    public boolean checkEmployeePayrollIsSync(String name) throws DatabaseException {
        List<Employee> employeeDataList = employeeServiceDb.getEmployeeData(name);
        return employeeDataList.get(0).equals((getEmployeePayrollData(name)));
    }

    public List<Employee> readFromDataBaseWithParticularRange(IOService ioService, String date1, String date2) throws DatabaseException{
        List<Employee> employeeWithRange =null;
        if(ioService.equals(IOService.DB_IO)){
            employeeWithRange = employeeServiceDb.readDataInRange(date1,date2);
        }
        return employeeWithRange;
    }


    public HashMap<String, Integer> getMinMaxAverageSum(String gender) throws DatabaseException {
        HashMap<String,Integer> data = null;
        data = employeeServiceDb.readDataMinMaxAverage(gender);
        return data;
    }


    public void addEmployee(String name, int salary, LocalDate date, String gender) throws DatabaseException {
        employeeDataList.add(employeeServiceDb.addEmployeeToDB(name,salary,date,gender));
    }


    private Employee getEmployeePayrollData(String name) {
        return this.employeeDataList.stream()
                .filter(employeeData -> employeeData.name.equals(name))
                .findFirst()
                .orElse(null);

    }

}
