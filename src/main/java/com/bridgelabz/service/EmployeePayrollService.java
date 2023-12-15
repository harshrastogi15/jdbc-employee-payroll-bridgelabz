package com.bridgelabz.service;

import com.bridgelabz.entity.Employee;
import com.bridgelabz.exception.DatabaseException;

import java.sql.SQLException;
import java.util.List;

public class EmployeePayrollService {
    public enum IOService{CONSOLE_IO, FILE_IO, DB_IO, REST_IO;}

    List<Employee> employeeDataList;
    public EmployeePayrollService(){}
    public List<Employee> readFromDataBase(IOService ioService) throws DatabaseException{

        if(ioService.equals(IOService.DB_IO)){
            this.employeeDataList = new EmployeeServiceDB().readData();
        }
        return this.employeeDataList;
    }
}
