package javapractice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeePayrollFileIOService {
    public static String PAYROLL_FILE_NAME = "src/test/resources/payroll-file.txt";

    public void write(List<EmployeePayrollData> employeePayrollList) {
        StringBuffer empBuffer = new StringBuffer();
        employeePayrollList.forEach(employee -> {
            String empDataString = employee.toString().concat("\n");
            empBuffer.append(empDataString);
        });
        try {
            Files.write(Paths.get(PAYROLL_FILE_NAME), empBuffer.toString().getBytes());
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    public void printData() {
        try {
            Files.lines(new File(PAYROLL_FILE_NAME).toPath())
                    .forEach(System.out::println);
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    public long countEntries() {
        long entries = 0;
        try {
            entries = Files.lines(new File(PAYROLL_FILE_NAME).toPath())
                    .count();
        } catch (IOException x) {
            x.printStackTrace();
        }
        return entries;
    }

    public List<EmployeePayrollData> readData() {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try {
            List<String> lines = Files.lines(new File(PAYROLL_FILE_NAME).toPath())
                    .map(line -> line.trim())
                    .collect(Collectors.toList());
            for (String line : lines) {
                String[] value = new String[3];
                String[] details = line.split(" ");
                for (int i = 1, j = 0; i < details.length; i = i + 3) {
                    value[j] = details[i];
                    j++;
                }
                EmployeePayrollData employeePayrollData = new EmployeePayrollData(Integer.parseInt(value[0]), value[1], Double.parseDouble(value[2]));
                employeePayrollList.add(employeePayrollData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }
}

