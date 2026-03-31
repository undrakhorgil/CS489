package edu.miu.cs.cs489appsd.lab2b;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.miu.cs.cs489appsd.lab2b.model.Patient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;

public class PAMSApp {
    public static void main(String[] args) throws Exception {
        Patient[] patients = loadPatients();

        var sorted = Arrays.stream(patients)
                .sorted(Comparator.comparingInt(Patient::getAge).reversed())
                .toList();

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        Path outDir = Path.of("lab2b", "out");
        Files.createDirectories(outDir);
        Path outFile = outDir.resolve("patients.json");

        mapper.writeValue(outFile.toFile(), sorted);

        System.out.println("Wrote JSON output to: " + outFile.toAbsolutePath());
    }

    private static Patient[] loadPatients() {
        return new Patient[] {
                new Patient(
                        1L,
                        "Daniel",
                        "Agar",
                        "(641) 123-0009",
                        "dagar@m.as",
                        "1 N Street",
                        LocalDate.parse("1987-01-19")
                ),
                new Patient(
                        2L,
                        "Ana",
                        "Smith",
                        null,
                        "amsith@te.edu",
                        null,
                        LocalDate.parse("1948-12-05")
                ),
                new Patient(
                        3L,
                        "Marcus",
                        "Garvey",
                        "(123) 292-0018",
                        null,
                        "4 East Ave",
                        LocalDate.parse("2001-09-18")
                ),
                new Patient(
                        4L,
                        "Jeff",
                        "Goldbloom",
                        "(999) 165-1192",
                        "jgold@es.co.za",
                        null,
                        LocalDate.parse("1995-02-28")
                ),
                new Patient(
                        5L,
                        "Mary",
                        "Washington",
                        null,
                        null,
                        "30 W Burlington",
                        LocalDate.parse("1932-05-31")
                )
        };
    }
}
