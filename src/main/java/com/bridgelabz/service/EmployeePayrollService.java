package com.bridgelabz.service;

import com.bridgelabz.entity.Employee;
import com.bridgelabz.exception.DatabaseException;

import java.sql.SQLException;
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


    private Employee getEmployeePayrollData(String name) {
        return this.employeeDataList.stream()
                .filter(employeeData -> employeeData.name.equals(name))
                .findFirst()
                .orElse(null);

    }

}
