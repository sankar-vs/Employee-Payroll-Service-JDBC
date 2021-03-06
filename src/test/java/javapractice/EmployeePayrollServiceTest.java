package javapractice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static javapractice.EmployeePayrollService.IOService.*;

public class EmployeePayrollServiceTest {
    @Test
    public  void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(1, "Athif",100000.0),
                new EmployeePayrollData(2, "Ashish",150000.0),
                new EmployeePayrollData(3, "Kevin",200000.0),
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayrollData(FILE_IO);
        employeePayrollService.printData(FILE_IO);
        long entries = employeePayrollService.countEntries(FILE_IO);
        Assertions.assertEquals(3, entries);
    }

    @Test
    public void givenFileOnReadingFromFileShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        long entries = employeePayrollService.readEmployeePayrollData(FILE_IO);
        Assertions.assertEquals(3, entries);
    }

    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readDBEmployeePayrollData(DB_IO);
        Assertions.assertEquals(5, employeePayrollData.size());
    }

    @Test
    public void testConnectivity() throws SQLException {
        EmployeePayrollDBService employeePayrollDBService = new EmployeePayrollDBService();
        Connection connectivity = employeePayrollDBService.getConnection();
        Assertions.assertNotNull(connectivity);
    }

    @Test
    void givenNewSalaryForEmployee_whenUpdated_shouldSyncWithDB() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readDBEmployeePayrollData(DB_IO);
        employeePayrollService.updateEmployeeSalary("Terisa", 3000000.0);
        boolean result = employeePayrollService.checkEmployeePayRollSyncWithDB("Terisa");
        Assertions.assertTrue(result);
    }

    @Test
    void givenDateRangeToEmployeePayRollInDB_WhenRetrieved_ShouldMatchFilteredEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> filteredResult = employeePayrollService.readDateRangeDBPayrollData("2018-01-01", "2019-12-22");
        Assertions.assertEquals(2, filteredResult.size());
    }

    @Test
    void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperTable() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        Map<String, Double> result = employeePayrollService.filterDBPayrollData();
        Assertions.assertEquals(3333333.3333333335, result.get("M"));
        Assertions.assertEquals(4500000.0, result.get("F"));
    }

    @Test
    void givenNewEmployeeToEmployeeRollDB_whenAdded_shouldSyncWithDB () throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readDBEmployeePayrollData(DB_IO);
        employeePayrollService.addEmployeeData("Alice","F",1000000.00, LocalDate.now(), new String[] {"HR", "Marketing"});
        boolean result = employeePayrollService.checkEmployeePayRollSyncWithDB("Alice");
        Assertions.assertTrue(result);
    }

    @Test
    void givenEmployeeNameToEmployeeRollDB_shouldDelete_shouldSyncWithDB () throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readDBEmployeePayrollData(DB_IO);
        employeePayrollService.deleteEmployeeData("Alice");
        List<EmployeePayrollData> employeePayrollDataDeleted = employeePayrollService.readDBEmployeePayrollData(DB_IO);
        Assertions.assertNull(employeePayrollDataDeleted.stream()
                .filter(e -> e.name.equals("Alice"))
                .findFirst().orElse(null));
    }
    @Test
    void givenNewEmployeeToEmployeeRollDB_whenAdded_shouldMatchWithEntries () {
        EmployeePayrollData[] payrollData = {
                new EmployeePayrollData(0, "Jeff", "M", 1000000.00, LocalDate.now(), new String[] {"HR"}),
                new EmployeePayrollData(0, "Bill", "M", 2000000.00, LocalDate.now(), new String[] {"QM"}),
                new EmployeePayrollData(0, "Sunder", "M", 4000000.00, LocalDate.now(), new String[] {"Sales"}),
                new EmployeePayrollData(0, "Mukesh", "M", 4400000.00, LocalDate.now(), new String[] {"Marketing"}),
                new EmployeePayrollData(0, "Anil", "M", 5000000.00, LocalDate.now(), new String[] {"QM"}),
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readDBEmployeePayrollData(DB_IO);
        Instant start = Instant.now();
        employeePayrollService.addEmployeesToPayroll(Arrays.asList(payrollData));
        Instant end = Instant.now();
        System.out.println("Duration without thread  "+ Duration.between(start, end));
    }

    @Test
    void givenNewEmployeeToEmployeeRollDB_whenAdded_shouldMatchWithEntriesUsingThread () {
        EmployeePayrollData[] payrollData = {
                new EmployeePayrollData(0, "Jeff", "M", 1000000.00, LocalDate.now(), new String[] {"HR"}),
                new EmployeePayrollData(0, "Bill", "M", 2000000.00, LocalDate.now(), new String[] {"QM"}),
                new EmployeePayrollData(0, "Sunder", "M", 4000000.00, LocalDate.now(), new String[] {"Sales"}),
                new EmployeePayrollData(0, "Mukesh", "M", 4400000.00, LocalDate.now(), new String[] {"Marketing"}),
                new EmployeePayrollData(0, "Anil", "M", 5000000.00, LocalDate.now(), new String[] {"QM"}),
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readDBEmployeePayrollData(DB_IO);
        Instant threadStart = Instant.now();
        employeePayrollService.addEmployeesToPayrollWithThreads(Arrays.asList(payrollData));
        Instant threadEnd = Instant.now();
        System.out.println("Duration with thread  "+ Duration.between(threadStart, threadEnd));
        Assertions.assertEquals(11,employeePayrollService.countEntries(DB_IO));
    }

}
