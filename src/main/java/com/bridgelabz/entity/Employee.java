package com.bridgelabz.entity;

import java.time.LocalDate;

public class Employee {
    public int id;
    public String name;
    public int salary;

    public LocalDate startDate;

    public Employee(int id, String name, int salary,LocalDate startDate){
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.startDate = startDate;
    }


    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Employee that = (Employee) o;
        return id == that.id && Integer.compare(salary,that.salary)==0 && name.equals(that.name);
    }

}
