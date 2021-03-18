package javapractice;

import java.time.LocalDate;

public class EmployeePayrollData {
    int id;
    String name;
    Double salary;
    LocalDate date;
    String gender;

    public EmployeePayrollData(int id, String name, String gender, Double salary, LocalDate date) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.salary = salary;
        this.date = date;
    }

    public EmployeePayrollData(int id, String name, Double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }


    public String toString() {
        return "ID: " + id +"  Name: " + name + "  Salary: " + salary;
    }
}
