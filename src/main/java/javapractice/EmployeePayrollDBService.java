package javapractice;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {
    private PreparedStatement employeePayRollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;

    private EmployeePayrollDBService(){}

    public static EmployeePayrollDBService getInstance(){
        if (employeePayrollDBService==null)
            employeePayrollDBService=new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    private Connection getConnection() throws SQLException {
        String jdbcULR = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String password = "root7433";
        Connection connection;
        System.out.println("Connecting To DB: " + jdbcULR);
        connection = DriverManager.getConnection(jdbcULR,userName,password);
        System.out.println("Connection is successful..! " + connection);
        return connection;
    }

    public List<EmployeePayrollData> readData() {
        String sql = "select * from employee_payroll;";
        List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
        try (Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollDataList = this.getEmployeePayRollData(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollDataList;
    }

    public List<EmployeePayrollData> getEmployeePayRollData(String name) {
        List<EmployeePayrollData>employeePayrollDataList = null;
        if (this.employeePayRollDataStatement==null)
            this.preparedStatementForEmployeeData();
        try {
            employeePayRollDataStatement.setString(1,name);
            ResultSet resultSet = employeePayRollDataStatement.executeQuery();
            employeePayrollDataList = this.getEmployeePayRollData(resultSet);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollDataList;
    }

    private List<EmployeePayrollData> getEmployeePayRollData(ResultSet resultSet) {
        List<EmployeePayrollData>employeePayrollDataList = new ArrayList<>();
        try {
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String gender = resultSet.getString("gender");
                double salary = resultSet.getDouble("salary");
                LocalDate date = resultSet.getDate("start").toLocalDate();
                employeePayrollDataList.add(new EmployeePayrollData(id,name,gender,salary,date));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollDataList;
    }

    private void preparedStatementForEmployeeData() {
        try {
            Connection connection = this.getConnection();
            String sql = "select * from employee_payroll where name= ?;";
            employeePayRollDataStatement = connection.prepareStatement(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int updateEmployeeData(String name, double salary) {
        return this.updateEmployeeDataUsingPreparedStatement(name, salary);
    }

    private int updateEmployeeDataUsingPreparedStatement(String name, double salary) {
        String sql = String.format("update employee_payroll set salary = %.2f where name = '%s';",salary,name);
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public List<EmployeePayrollData> getFilteredDateRangeResult(String dateBefore, String dateAfter) {
        String sql = String.format("select * from employee_payroll where start between '%s' and '%s';",dateBefore,dateAfter);
        List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
        try (Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollDataList = this.getEmployeePayRollData(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollDataList;
    }

    public Map<String, Double> getAverageSalaryGroupByGender() {
        String sql = "SELECT gender, AVG(salary) as avg_salary FROM employee_payroll GROUP BY gender; ";
        Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
        try (Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String gender = resultSet.getString("gender");
                double salary = resultSet.getDouble("avg_salary");
                genderToAverageSalaryMap.put(gender, salary);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return genderToAverageSalaryMap;
    }

    public EmployeePayrollData addEmployeeData(String name, String gender, double salary, LocalDate date) {
        int id = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try {
            connection = this.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = String.format("insert into employee_payroll (name,gender,salary,start) values ('%s','%s','%s','%s')", name, gender, salary, Date.valueOf(date));
        try (Statement statement = connection.createStatement()) {
            int rowAffected = statement.executeUpdate(sql,statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next())
                    id = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement()) {
            double deductions = salary * 0.2;
            double taxable_pay = salary - deductions;
            double tax = taxable_pay * 0.1;
            double netpay = salary - tax;
            sql = String.format("INSERT INTO netpay_payroll (employee_id, basic_pay, deductions, taxable_pay, tax, netpay)" +
                    " VALUES (%s,%s,%s,%s,%s,%s)", id, salary, deductions, taxable_pay, tax, netpay);
            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1) {
                employeePayrollData = new EmployeePayrollData(id, name, gender, salary, date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollData;
    }
}