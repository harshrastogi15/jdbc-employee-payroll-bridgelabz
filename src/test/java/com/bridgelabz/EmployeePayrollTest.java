package com.bridgelabz;

import com.bridgelabz.entity.Employee;
import com.bridgelabz.exception.DatabaseException;
import com.bridgelabz.service.EmployeePayrollService;
import com.bridgelabz.service.EmployeeServiceDB;

import org.junit.Assert;
import org.junit.Test;
import java.sql.SQLException;
import java.util.List;


public class EmployeePayrollTest {

    @Test
    public void payrollServiceDatabase_ConnectDatabase(){
        try {
            boolean checkConnection = new EmployeeServiceDB().checkConnectionToDB();
            Assert.assertTrue(checkConnection);
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount(){
        try {
            EmployeePayrollService employeePayrollService = new EmployeePayrollService();
            List<Employee> employeeDataList = employeePayrollService.readFromDataBase(EmployeePayrollService.IOService.DB_IO);
            Assert.assertEquals(4,employeeDataList.size());
        }catch (DatabaseException e){
            System.out.println(e.getMessage());
        }
    }

}
