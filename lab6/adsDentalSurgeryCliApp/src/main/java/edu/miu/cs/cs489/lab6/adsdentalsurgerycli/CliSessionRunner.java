package edu.miu.cs.cs489.lab6.adsdentalsurgerycli;

import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.bootstrap.SampleDataLoader;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.Address;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.Appointment;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.AppointmentChannel;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.AppointmentStatus;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.Dentist;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.Surgery;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.repository.AppointmentRepository;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.repository.PatientRepository;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.service.DentistService;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.service.SurgeryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Interactive CLI menu for CRUD and queries. Skipped when {@code test} profile is active.
 */
@Component
@Profile("!test")
public class CliSessionRunner implements CommandLineRunner {

    private static final DateTimeFormatter APPT_DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final SampleDataLoader sampleDataLoader;
    private final DentistService dentistService;
    private final SurgeryService surgeryService;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    public CliSessionRunner(
            SampleDataLoader sampleDataLoader,
            DentistService dentistService,
            SurgeryService surgeryService,
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository) {
        this.sampleDataLoader = sampleDataLoader;
        this.dentistService = dentistService;
        this.surgeryService = surgeryService;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== ADS Dental Surgeries — Lab 6 Spring Data JPA CLI ===");
        sampleDataLoader.loadIfEmpty();

        try (Scanner in = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMainMenu();
                System.out.print("Main » ");
                String line = readLine(in);
                switch (line) {
                    case "1" -> {
                        sampleDataLoader.loadIfEmpty();
                        System.out.println("(Sample data runs only when no dentists exist yet — use a fresh DB or truncate tables to reload.)");
                    }
                    case "2" -> dentistMenu(in);
                    case "3" -> surgeryMenu(in);
                    case "4" -> patientMenu(in);
                    case "5" -> appointmentMenu(in);
                    case "0" -> running = false;
                    default -> System.out.println("Unknown option. Choose 1–5 or 0.");
                }
            }
        }
        System.out.println("Goodbye.");
    }

    private static void printMainMenu() {
        System.out.println("""
                
                ========== Main menu ==========
                  1  Sample data (load if database is empty)
                  2  Dentists
                  3  Surgeries
                  4  Patients
                  5  Appointments
                  0  Exit
                """);
    }

    private void dentistMenu(Scanner in) {
        boolean back = false;
        while (!back) {
            System.out.println("""
                    
                    ---- Dentists ----
                      1  List all (sorted by last name)
                      2  Find by ID
                      3  Create
                      4  Update
                      5  Delete
                      0  Back to main menu
                    """);
            System.out.print("Dentists » ");
            switch (readLine(in)) {
                case "1" -> listDentists();
                case "2" -> findDentistById(in);
                case "3" -> createDentist(in);
                case "4" -> updateDentist(in);
                case "5" -> deleteDentist(in);
                case "0" -> back = true;
                default -> System.out.println("Unknown option. Choose 1–5 or 0.");
            }
        }
    }

    private void surgeryMenu(Scanner in) {
        boolean back = false;
        while (!back) {
            System.out.println("""
                    
                    ---- Surgeries ----
                      1  List all
                      2  Find by ID
                      3  Create
                      4  Update
                      5  Delete
                      0  Back to main menu
                    """);
            System.out.print("Surgeries » ");
            switch (readLine(in)) {
                case "1" -> listSurgeries();
                case "2" -> findSurgeryById(in);
                case "3" -> createSurgery(in);
                case "4" -> updateSurgery(in);
                case "5" -> deleteSurgery(in);
                case "0" -> back = true;
                default -> System.out.println("Unknown option. Choose 1–5 or 0.");
            }
        }
    }

    private void patientMenu(Scanner in) {
        boolean back = false;
        while (!back) {
            System.out.println("""
                    
                    ---- Patients ----
                      1  List all
                      2  Find by ID
                      0  Back to main menu
                    """);
            System.out.print("Patients » ");
            switch (readLine(in)) {
                case "1" -> listPatients();
                case "2" -> findPatientById(in);
                case "0" -> back = true;
                default -> System.out.println("Unknown option. Choose 1–2 or 0.");
            }
        }
    }

    private void appointmentMenu(Scanner in) {
        boolean back = false;
        while (!back) {
            System.out.println("""
                    
                    ---- Appointments ----
                      1  List all
                      2  List for a dentist (by email)
                      3  List SCHEDULED only
                      4  Create
                      0  Back to main menu
                    """);
            System.out.print("Appointments » ");
            switch (readLine(in)) {
                case "1" -> listAllAppointments();
                case "2" -> listAppointmentsForDentist(in);
                case "3" -> listScheduledAppointments();
                case "4" -> createAppointment(in);
                case "0" -> back = true;
                default -> System.out.println("Unknown option. Choose 1–4 or 0.");
            }
        }
    }

    private static String readLine(Scanner in) {
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }

    private void listDentists() {
        System.out.println("\n--- All dentists ---");
        CliTableFormatter.printDentistsTable(dentistService.findAllSortedByLastName());
    }

    private void findDentistById(Scanner in) {
        Long id = readLong(in, "Dentist ID: ");
        if (id == null) {
            return;
        }
        dentistService.findById(id).ifPresentOrElse(
                CliTableFormatter::printDentistDetail,
                () -> System.out.println("No dentist with id " + id));
    }

    private void createDentist(Scanner in) {
        System.out.println("--- New dentist ---");
        String first = prompt(in, "First name: ");
        String last = prompt(in, "Last name: ");
        String phone = prompt(in, "Phone: ");
        String email = prompt(in, "Email (unique): ");
        String spec = prompt(in, "Specialization: ");
        Dentist d = new Dentist(null, first, last, phone, email, spec);
        Dentist saved = dentistService.save(d);
        System.out.println("Saved.");
        CliTableFormatter.printDentistDetail(saved);
    }

    private void updateDentist(Scanner in) {
        Long id = readLong(in, "Dentist ID to update: ");
        if (id == null) {
            return;
        }
        var opt = dentistService.findById(id);
        if (opt.isEmpty()) {
            System.out.println("No dentist with id " + id);
            return;
        }
        Dentist d = opt.get();
        System.out.println("Current record:");
        CliTableFormatter.printDentistDetail(d);
        System.out.println("(Press Enter to keep a field unchanged.)");
        String first = promptOrKeep(in, "First name", d.getFirstName());
        String last = promptOrKeep(in, "Last name", d.getLastName());
        String phone = promptOrKeep(in, "Phone", d.getContactPhoneNumber());
        String email = promptOrKeep(in, "Email", d.getEmail());
        String spec = promptOrKeep(in, "Specialization", d.getSpecialization());
        d.setFirstName(first);
        d.setLastName(last);
        d.setContactPhoneNumber(phone);
        d.setEmail(email);
        d.setSpecialization(spec);
        Dentist updated = dentistService.update(d);
        System.out.println("Updated.");
        CliTableFormatter.printDentistDetail(updated);
    }

    private void deleteDentist(Scanner in) {
        Long id = readLong(in, "Dentist ID to delete: ");
        if (id == null) {
            return;
        }
        if (dentistService.findById(id).isEmpty()) {
            System.out.println("No dentist with id " + id);
            return;
        }
        String yes = prompt(in, "Type YES to delete: ");
        if (!"YES".equalsIgnoreCase(yes)) {
            System.out.println("Cancelled.");
            return;
        }
        try {
            dentistService.deleteById(id);
            System.out.println("Deleted dentist id " + id);
        } catch (Exception e) {
            System.out.println("Delete failed (foreign key to appointments?): " + e.getMessage());
        }
    }

    private void listSurgeries() {
        System.out.println("\n--- All surgeries ---");
        CliTableFormatter.printSurgeriesTable(surgeryService.findAll());
    }

    private void findSurgeryById(Scanner in) {
        Long id = readLong(in, "Surgery ID: ");
        if (id == null) {
            return;
        }
        surgeryService.findById(id).ifPresentOrElse(
                CliTableFormatter::printSurgeryDetail,
                () -> System.out.println("No surgery with id " + id));
    }

    private void createSurgery(Scanner in) {
        System.out.println("--- New surgery ---");
        String name = prompt(in, "Name / room code (e.g. S15): ");
        String street = prompt(in, "Address street: ");
        String city = prompt(in, "Address city: ");
        String state = prompt(in, "Address state/region: ");
        String zip = prompt(in, "Zip (optional, Enter to skip): ");
        if (zip.isBlank()) {
            zip = null;
        }
        String tel = prompt(in, "Telephone: ");
        Address addr = new Address(null, street, city, state, zip);
        Surgery s = new Surgery(null, name, addr, tel);
        Surgery saved = surgeryService.save(s);
        System.out.println("Saved.");
        CliTableFormatter.printSurgeryDetail(saved);
    }

    private void updateSurgery(Scanner in) {
        Long id = readLong(in, "Surgery ID to update: ");
        if (id == null) {
            return;
        }
        var opt = surgeryService.findById(id);
        if (opt.isEmpty()) {
            System.out.println("No surgery with id " + id);
            return;
        }
        Surgery s = opt.get();
        System.out.println("Current record:");
        CliTableFormatter.printSurgeryDetail(s);
        System.out.println("(Press Enter to keep name / phone; address: answer y to replace.)");
        String name = promptOrKeep(in, "Name", s.getName());
        String tel = promptOrKeep(in, "Telephone", s.getTelephoneNumber());
        s.setName(name);
        s.setTelephoneNumber(tel);
        String addrChoice = prompt(in, "Update address? (y/N): ");
        if (addrChoice.equalsIgnoreCase("y")) {
            String street = prompt(in, "Street: ");
            String city = prompt(in, "City: ");
            String state = prompt(in, "State: ");
            String zip = prompt(in, "Zip (optional): ");
            s.setLocationAddress(new Address(null, street, city, state, zip.isBlank() ? null : zip));
        }
        Surgery updated = surgeryService.update(s);
        System.out.println("Updated.");
        CliTableFormatter.printSurgeryDetail(updated);
    }

    private void deleteSurgery(Scanner in) {
        Long id = readLong(in, "Surgery ID to delete: ");
        if (id == null) {
            return;
        }
        if (surgeryService.findById(id).isEmpty()) {
            System.out.println("No surgery with id " + id);
            return;
        }
        String yes = prompt(in, "Type YES to delete: ");
        if (!"YES".equalsIgnoreCase(yes)) {
            System.out.println("Cancelled.");
            return;
        }
        try {
            surgeryService.deleteById(id);
            System.out.println("Deleted surgery id " + id);
        } catch (Exception e) {
            System.out.println("Delete failed (foreign key to appointments?): " + e.getMessage());
        }
    }

    private void listPatients() {
        System.out.println("\n--- All patients ---");
        CliTableFormatter.printPatientsTable(patientRepository.findAll());
    }

    private void findPatientById(Scanner in) {
        Long id = readLong(in, "Patient ID: ");
        if (id == null) {
            return;
        }
        patientRepository.findById(id).ifPresentOrElse(
                CliTableFormatter::printPatientDetail,
                () -> System.out.println("No patient with id " + id));
    }

    private void listAllAppointments() {
        System.out.println("\n--- All appointments ---");
        CliTableFormatter.printAppointmentsTable(appointmentRepository.findAll(), true);
    }

    private void listAppointmentsForDentist(Scanner in) {
        String email = prompt(in, "Dentist email: ");
        dentistService.findByEmail(email).ifPresentOrElse(
                d -> {
                    System.out.println("\n--- Appointments for " + d.getFirstName() + " " + d.getLastName() + " ---");
                    var list = appointmentRepository.findByDentist_DentistIdOrderByStartAtAsc(d.getDentistId());
                    CliTableFormatter.printAppointmentsTable(list, true);
                },
                () -> System.out.println("No dentist with that email."));
    }

    private void listScheduledAppointments() {
        System.out.println("\n--- SCHEDULED appointments ---");
        CliTableFormatter.printAppointmentsTable(
                appointmentRepository.findByStatusOrderByStartAtAsc(AppointmentStatus.SCHEDULED), true);
    }

    private void createAppointment(Scanner in) {
        System.out.println("--- New appointment ---");
        Long patientId = readLong(in, "Patient ID: ");
        if (patientId == null) {
            return;
        }
        var patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isEmpty()) {
            System.out.println("No patient with id " + patientId);
            return;
        }
        Long dentistId = readLong(in, "Dentist ID: ");
        if (dentistId == null) {
            return;
        }
        var dentistOpt = dentistService.findById(dentistId);
        if (dentistOpt.isEmpty()) {
            System.out.println("No dentist with id " + dentistId);
            return;
        }
        Long surgeryId = readLong(in, "Surgery ID: ");
        if (surgeryId == null) {
            return;
        }
        var surgeryOpt = surgeryService.findById(surgeryId);
        if (surgeryOpt.isEmpty()) {
            System.out.println("No surgery with id " + surgeryId);
            return;
        }
        LocalDateTime startAt = readLocalDateTime(in, "Start date/time (yyyy-MM-dd HH:mm): ");
        if (startAt == null) {
            return;
        }
        String proposedLine = prompt(in, "Proposed start (optional, same format, Enter to skip): ");
        LocalDateTime proposedStart = null;
        if (!proposedLine.isBlank()) {
            try {
                proposedStart = LocalDateTime.parse(proposedLine, APPT_DT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid proposed time — left blank.");
            }
        }
        String statusLine = prompt(in, "Status (REQUESTED/SCHEDULED/CANCELLED/RESCHEDULE_REQUESTED/COMPLETED) [SCHEDULED]: ");
        AppointmentStatus status = parseAppointmentStatus(statusLine);
        String channelLine = prompt(in, "Channel (PHONE/ONLINE) [PHONE]: ");
        AppointmentChannel channel = parseAppointmentChannel(channelLine);
        Appointment appt = new Appointment(
                null,
                patientOpt.get(),
                dentistOpt.get(),
                surgeryOpt.get(),
                startAt,
                proposedStart,
                status,
                channel);
        Appointment saved = appointmentRepository.save(appt);
        System.out.println("Saved.");
        CliTableFormatter.printAppointmentDetail(saved);
    }

    private static AppointmentStatus parseAppointmentStatus(String line) {
        if (line == null || line.isBlank()) {
            return AppointmentStatus.SCHEDULED;
        }
        try {
            return AppointmentStatus.valueOf(line.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown status — using SCHEDULED.");
            return AppointmentStatus.SCHEDULED;
        }
    }

    private static AppointmentChannel parseAppointmentChannel(String line) {
        if (line == null || line.isBlank()) {
            return AppointmentChannel.PHONE;
        }
        try {
            return AppointmentChannel.valueOf(line.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown channel — using PHONE.");
            return AppointmentChannel.PHONE;
        }
    }

    private static LocalDateTime readLocalDateTime(Scanner in, String label) {
        while (true) {
            System.out.print(label);
            if (!in.hasNextLine()) {
                return null;
            }
            String s = in.nextLine().trim();
            try {
                return LocalDateTime.parse(s, APPT_DT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Use yyyy-MM-dd HH:mm (e.g. 2013-09-10 09:00)");
            }
        }
    }

    private static String prompt(Scanner in, String label) {
        System.out.print(label);
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }

    private static String promptOrKeep(Scanner in, String label, String current) {
        System.out.print(label + " [" + current + "]: ");
        if (!in.hasNextLine()) {
            return current;
        }
        String v = in.nextLine().trim();
        return v.isEmpty() ? current : v;
    }

    private static Long readLong(Scanner in, String label) {
        System.out.print(label);
        if (!in.hasNextLine()) {
            return null;
        }
        String s = in.nextLine().trim();
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return null;
        }
    }
}
