package javapractice;

import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData {
    int id;
    String name;
    Double salary;
    LocalDate date;
    String gender;String[] department;

    public EmployeePayrollData(int id, String name, String gender, Double salary, LocalDate date, String[] department) {
        this(id, name, gender, salary, date);
        this.department = department;
    }

    public EmployeePayrollData(int id, String name, String gender, Double salary, LocalDate date) {
        this(id, name, salary);
        this.gender = gender;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePayrollData that = (EmployeePayrollData) o;
        return id == that.id && Objects.equals(name, that.name)
                && Objects.equals(salary, that.salary) && Objects.equals(date, that.date)
                && Objects.equals(gender, that.gender);
    }

}
