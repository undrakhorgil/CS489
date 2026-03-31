package edu.miu.cs.cs489appsd.lab2a;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.miu.cs.cs489appsd.lab2a.model.Employee;
import edu.miu.cs.cs489appsd.lab2a.model.PensionPlan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EmployeePensionsCliApp {
    public static void main(String[] args) throws Exception {
        var employees = loadEmployees();

        System.out.println("=== All Employees (sorted by salary desc, last name asc) ===");
        System.out.println(toPrettyJson(employeesSortedForAllEmployeesReport(employees)));

        System.out.println();
        System.out.println("=== Quarterly Upcoming Enrollees (next quarter) ===");
        System.out.println(toPrettyJson(quarterlyUpcomingEnrollees(employees, LocalDate.now())));
    }

    private static String toPrettyJson(Object value) throws Exception {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(value);
    }

    private static List<Employee> employeesSortedForAllEmployeesReport(List<Employee> employees) {
        return employees.stream()
                .sorted(Comparator
                        .comparing(Employee::getYearlySalary, Comparator.reverseOrder())
                        .thenComparing(Employee::getLastName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }

    static List<Employee> quarterlyUpcomingEnrollees(List<Employee> employees, LocalDate today) {
        var nextQuarter = nextQuarterBounds(today);
        LocalDate start = nextQuarter.start();
        LocalDate end = nextQuarter.end();

        return employees.stream()
                .filter(e -> e.getPensionPlan() == null)
                .filter(e -> {
                    LocalDate anniversary = e.getEmploymentDate().plusYears(3);
                    return (!anniversary.isBefore(start)) && (!anniversary.isAfter(end));
                })
                .sorted(Comparator.comparing(Employee::getEmploymentDate).reversed())
                .toList();
    }

    private record QuarterBounds(LocalDate start, LocalDate end) {
    }

    private static QuarterBounds nextQuarterBounds(LocalDate today) {
        int month = today.getMonthValue();
        int currentQuarter = ((month - 1) / 3) + 1;
        int nextQuarter = currentQuarter == 4 ? 1 : currentQuarter + 1;
        int year = currentQuarter == 4 ? today.getYear() + 1 : today.getYear();

        Month startMonth = switch (nextQuarter) {
            case 1 -> Month.JANUARY;
            case 2 -> Month.APRIL;
            case 3 -> Month.JULY;
            case 4 -> Month.OCTOBER;
            default -> throw new IllegalStateException("Unexpected quarter: " + nextQuarter);
        };

        LocalDate start = LocalDate.of(year, startMonth, 1);
        LocalDate end = start.plusMonths(3).minusDays(1);
        return new QuarterBounds(start, end);
    }

    private static List<Employee> loadEmployees() {
        List<Employee> employees = new ArrayList<>();

        employees.add(new Employee(
                1L,
                "Daniel",
                "Agar",
                LocalDate.parse("2023-01-17"),
                new BigDecimal("105945.50"),
                null
        ));

        employees.add(new Employee(
                2L,
                "Benard",
                "Shaw",
                LocalDate.parse("2022-09-03"),
                new BigDecimal("197750.00"),
                null
        ));

        employees.add(new Employee(
                3L,
                "Carly",
                "Agar",
                LocalDate.parse("2014-05-16"),
                new BigDecimal("842000.75"),
                new PensionPlan("SM2307", LocalDate.parse("2017-05-17"), new BigDecimal("1555.50"))
        ));

        employees.add(new Employee(
                4L,
                "Wesley",
                "Schneider",
                LocalDate.parse("2023-07-21"),
                new BigDecimal("74500.00"),
                null
        ));

        employees.add(new Employee(
                5L,
                "Anna",
                "Wiltord",
                LocalDate.parse("2023-03-15"),
                new BigDecimal("85750.00"),
                null
        ));

        employees.add(new Employee(
                6L,
                "Yosef",
                "Tesfalem",
                LocalDate.parse("2024-10-31"),
                new BigDecimal("100000.00"),
                null
        ));

        return employees;
    }
}
