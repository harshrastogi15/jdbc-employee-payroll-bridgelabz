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

}
