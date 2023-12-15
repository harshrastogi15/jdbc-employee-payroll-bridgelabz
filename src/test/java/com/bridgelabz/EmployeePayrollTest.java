package com.bridgelabz;

import com.bridgelabz.entity.Employee;
import com.bridgelabz.exception.DatabaseException;
import com.bridgelabz.service.EmployeePayrollService;
import com.bridgelabz.service.EmployeeServiceDB;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EmployeePayrollTest {

    @Test
    public void payrollServiceDatabase_ConnectDatabase(){
        try {
            boolean checkConnection = EmployeeServiceDB.getInstance().checkConnectionToDB();
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

    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncDB(){
        try {

            EmployeePayrollService employeePayrollService = new EmployeePayrollService();
            List<Employee> employeeDataList = employeePayrollService.readFromDataBase(EmployeePayrollService.IOService.DB_IO);
            employeePayrollService.updateSalary("Alice",200000);
            boolean result = employeePayrollService.checkEmployeePayrollIsSync("Alice");
            System.out.println(result);
            Assert.assertTrue(result);
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void retrieveEmployeesWhoHaveJoined_ParticularDataRange(){
        try {
            EmployeePayrollService employeePayrollService = new EmployeePayrollService();
            List<Employee> employeeDataList = employeePayrollService.readFromDataBaseWithParticularRange(EmployeePayrollService.IOService.DB_IO,"2022-01-01","2024-01-01");
            Assert.assertEquals(1,employeeDataList.size());
        }catch (DatabaseException e){
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void findSumAverageMinMax_Number_MaleFemaleEmployees(){
        try{
            EmployeePayrollService employeePayrollService = new EmployeePayrollService();
            HashMap<String,Integer> data = employeePayrollService.getMinMaxAverageSum("F");
            System.out.println(data);
            Assert.assertEquals((Integer) 200000,data.get("MAX"));
        }catch (DatabaseException e){
            System.out.println(e.getMessage());
        }
    }
}
