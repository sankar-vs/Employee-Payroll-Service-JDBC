package javapractice;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class EmployeePayrollService {

    public enum IOService {CONSOLE_IO, FILE_IO, DB_IO, REST_IO}
    private List<EmployeePayrollData> employeePayrollList;
    private static EmployeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService() {
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList){
        this();
        this.employeePayrollList = employeePayrollList;
    }

    private void readEmployeePayrollData(Scanner consoleInputReader) {
        System.out.println("Enter id: ");
        int id = consoleInputReader.nextInt();
        System.out.println("Enter Name: ");
        String name = consoleInputReader.next();
        System.out.println("Enter Salary: ");
        Double salary = consoleInputReader.nextDouble();
        employeePayrollList.add(new EmployeePayrollData(id, name, salary));
    }

    public long readEmployeePayrollData(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            this.employeePayrollList = new EmployeePayrollFileIOService().readData();
        return employeePayrollList.size();
    }

    public void writeEmployeePayrollData(IOService ioService) {
        if(ioService.equals(IOService.CONSOLE_IO))
            employeePayrollList.stream().forEach(System.out::println);
        else if (ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().write(employeePayrollList);
    }

    public void printData(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().printData();
    }

    public long countEntries(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return employeePayrollList.size();
    }

    public List<EmployeePayrollData> readDBEmployeePayrollData(IOService ioService) {
        if (ioService.equals(IOService.DB_IO))
            this.employeePayrollList = employeePayrollDBService.readData();
        return employeePayrollList;
    }

    public void updateEmployeeSalary(String name, double salary) {
        int result = employeePayrollDBService.updateEmployeeData(name,salary);
        if (result == 0) return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayRollData(name);
        if (employeePayrollData != null) employeePayrollData.salary= salary;
    }

    private EmployeePayrollData getEmployeePayRollData(String name) {
        return this.employeePayrollList.stream()
                .filter(e -> e.name.equals(name))
                .findFirst().orElse(null);
    }

    public boolean checkEmployeePayRollSyncWithDB(String name) {
        List<EmployeePayrollData>employeePayrollDataList= employeePayrollDBService.getEmployeePayRollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayRollData(name));
    }

    public List<EmployeePayrollData> readDateRangeDBPayrollData(String dateBefore, String dateAfter) {
        return employeePayrollDBService.getFilteredDateRangeResult(dateBefore, dateAfter);
    }

    public Map<String, Double> filterDBPayrollData() {
        return employeePayrollDBService.getAverageSalaryGroupByGender();
    }

    public void addEmployeeData(String name, String gender, double salary, LocalDate date, String[] department) throws SQLException {
        employeePayrollList.add(employeePayrollDBService.addEmployeeData(name,gender,salary,date,department));
    }

    public void deleteEmployeeData(String name) throws SQLException {
        employeePayrollList.remove(employeePayrollDBService.removeEmployeeData(name, employeePayrollList));
    }


    public void addEmployeesToPayroll(List<EmployeePayrollData> employeePayrollDataList) {
        employeePayrollDataList.forEach(x -> {
            try {
                this.addEmployeeData(x.name, x.gender, x.salary, x .date, x.department);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void addEmployeesToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList) {
        Map<Integer,Boolean> empAdditionStatus = new HashMap<Integer,Boolean>();
        employeePayrollDataList.forEach(x -> {
            Runnable task = () ->{
                empAdditionStatus.put(x.hashCode(),false);
                System.out.println("Employee Being Added : " + Thread.currentThread().getName());
                try {
                    this.addEmployeeData(x.name, x.gender, x.salary, x .date, x.department);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                empAdditionStatus.put(x.hashCode(),true);
                System.out.println("Employee Being Added : " + Thread.currentThread().getName());
            };
            Thread thread = new Thread(task, x.name);
            thread.start();
        });
        while (empAdditionStatus.containsValue(false)){
            try{Thread.sleep(10);
            }catch  (InterruptedException e){}
        }
        System.out.println(employeePayrollDataList);
    }

}
