package com.bridgelabz;

import com.bridgelabz.exception.DatabaseException;
import com.bridgelabz.service.EmployeeServiceDB;
import org.junit.Assert;
import org.junit.Test;

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

}
