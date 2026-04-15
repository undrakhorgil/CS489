package edu.miu.cs.cs489.lab6.adsdentalsurgerycli;

import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Readable CLI tables and detail blocks. Clears the screen before list views; columns are fixed-width padded.
 */
public final class CliTableFormatter {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    /** ANSI: clear screen and move cursor home (common in macOS / Linux / many IDE terminals). */
    private static final String CLEAR_SCREEN = "\u001B[2J\u001B[H";

    private CliTableFormatter() {
    }

    /** Clear terminal content before a fresh list (no-op if ANSI unsupported). */
    public static void clearListingArea() {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
    }

    public static void printDentistsTable(List<Dentist> dentists) {
        clearListingArea();
        if (dentists.isEmpty()) {
            System.out.println("(no dentists)");
            return;
        }
        int[] w = {6, 16, 16, 16, 38, 32};
        String[] heads = {"ID", "Last name", "First name", "Phone", "Email", "Specialization"};
        hr(w);
        row(w, heads);
        hr(w);
        for (Dentist d : dentists) {
            row(w,
                    String.valueOf(d.getDentistId()),
                    nz(d.getLastName()),
                    nz(d.getFirstName()),
                    nz(d.getContactPhoneNumber()),
                    nz(d.getEmail()),
                    nz(d.getSpecialization()));
        }
        hr(w);
        System.out.println(dentists.size() + " row(s)");
    }

    public static void printDentistDetail(Dentist d) {
        System.out.println();
        rule(52);
        System.out.println("  Dentist");
        rule(52);
        kv("ID", d.getDentistId());
        kv("Name", d.getFirstName() + " " + d.getLastName());
        kv("Phone", d.getContactPhoneNumber());
        kv("Email", d.getEmail());
        kv("Specialization", d.getSpecialization());
        rule(52);
    }

    public static void printSurgeriesTable(List<Surgery> surgeries) {
        clearListingArea();
        if (surgeries.isEmpty()) {
            System.out.println("(no surgeries)");
            return;
        }
        int[] w = {6, 12, 18, 46};
        hr(w);
        row(w, "ID", "Room", "Phone", "Location (address)");
        hr(w);
        for (Surgery s : surgeries) {
            Address a = s.getLocationAddress();
            String loc = a == null ? "—" : nz(a.getStreet()) + ", " + nz(a.getCity());
            row(w,
                    String.valueOf(s.getSurgeryId()),
                    nz(s.getName()),
                    nz(s.getTelephoneNumber()),
                    loc);
        }
        hr(w);
        System.out.println(surgeries.size() + " row(s)");
    }

    public static void printSurgeryDetail(Surgery s) {
        System.out.println();
        rule(52);
        System.out.println("  Surgery");
        rule(52);
        kv("ID", s.getSurgeryId());
        kv("Name / room", s.getName());
        kv("Telephone", s.getTelephoneNumber());
        if (s.getLocationAddress() != null) {
            Address a = s.getLocationAddress();
            kv("Street", a.getStreet());
            kv("City", a.getCity());
            kv("State", a.getState());
            kv("Zip", a.getZipCode() != null ? a.getZipCode() : "—");
        }
        rule(52);
    }

    public static void printPatientsTable(List<Patient> patients) {
        clearListingArea();
        if (patients.isEmpty()) {
            System.out.println("(no patients)");
            return;
        }
        int[] w = {6, 10, 22, 36, 12};
        hr(w);
        row(w, "ID", "Pat ref", "Name", "Email", "DOB");
        hr(w);
        for (Patient p : patients) {
            String ref = p.getPatientRef() != null ? p.getPatientRef() : "—";
            String name = p.getFirstName() + " " + p.getLastName();
            row(w,
                    String.valueOf(p.getPatientId()),
                    ref,
                    name,
                    nz(p.getEmail()),
                    p.getDateOfBirth() != null ? p.getDateOfBirth().toString() : "—");
        }
        hr(w);
        System.out.println(patients.size() + " row(s)");
    }

    public static void printPatientDetail(Patient p) {
        System.out.println();
        rule(52);
        System.out.println("  Patient");
        rule(52);
        kv("ID", p.getPatientId());
        kv("Patient ref", p.getPatientRef() != null ? p.getPatientRef() : "—");
        kv("Name", p.getFirstName() + " " + p.getLastName());
        kv("Phone", p.getContactPhoneNumber());
        kv("Email", p.getEmail());
        kv("Date of birth", p.getDateOfBirth());
        if (p.getMailingAddress() != null) {
            Address a = p.getMailingAddress();
            kv("Mailing", a.getStreet() + ", " + a.getCity() + ", " + a.getState());
        }
        rule(52);
    }

    public static void printAppointmentDetail(Appointment a) {
        System.out.println();
        rule(52);
        System.out.println("  Appointment");
        rule(52);
        kv("ID", a.getAppointmentId());
        kv("Start", a.getStartAt() != null ? a.getStartAt().format(DT) : "—");
        kv("Proposed start", a.getProposedStartAt() != null ? a.getProposedStartAt().format(DT) : "—");
        kv("Status", a.getStatus());
        kv("Channel", a.getChannel());
        if (a.getPatient() != null) {
            Patient p = a.getPatient();
            kv("Patient", p.getFirstName() + " " + p.getLastName() + " (id " + p.getPatientId() + ")");
        }
        if (a.getDentist() != null) {
            Dentist d = a.getDentist();
            kv("Dentist", d.getFirstName() + " " + d.getLastName() + " (id " + d.getDentistId() + ")");
        }
        if (a.getSurgery() != null) {
            kv("Surgery", a.getSurgery().getName() + " (id " + a.getSurgery().getSurgeryId() + ")");
        }
        rule(52);
    }

    public static void printAppointmentsTable(List<Appointment> appointments, boolean includeSurgery) {
        clearListingArea();
        if (appointments.isEmpty()) {
            System.out.println("(no appointments)");
            return;
        }
        if (includeSurgery) {
            int[] w = {6, 18, 12, 24, 20, 10};
            hr(w);
            row(w, "ID", "Start", "Status", "Patient", "Dentist", "Surgery");
            hr(w);
            for (Appointment a : appointments) {
                String pat = a.getPatient().getFirstName() + " " + a.getPatient().getLastName();
                String den = a.getDentist().getFirstName() + " " + a.getDentist().getLastName();
                row(w,
                        String.valueOf(a.getAppointmentId()),
                        a.getStartAt().format(DT),
                        a.getStatus().name(),
                        pat,
                        den,
                        nz(a.getSurgery().getName()));
            }
            hr(w);
        } else {
            int[] w = {6, 18, 14, 24, 20};
            hr(w);
            row(w, "ID", "Start", "Status", "Patient", "Dentist");
            hr(w);
            for (Appointment a : appointments) {
                String pat = a.getPatient().getFirstName() + " " + a.getPatient().getLastName();
                String den = a.getDentist().getFirstName() + " " + a.getDentist().getLastName();
                row(w,
                        String.valueOf(a.getAppointmentId()),
                        a.getStartAt().format(DT),
                        a.getStatus().name(),
                        pat,
                        den);
            }
            hr(w);
        }
        System.out.println(appointments.size() + " row(s)");
    }

    private static void row(int[] widths, String... cells) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < widths.length; i++) {
            String raw = i < cells.length ? cells[i] : "";
            sb.append(' ').append(fit(raw, widths[i])).append(" |");
        }
        System.out.println(sb);
    }

    /** Horizontal rule matching {@link #row} width: {@code |} + each {@code (space + w + " |")}. */
    private static void hr(int[] widths) {
        int len = 1;
        for (int w : widths) {
            len += 1 + w + 3;
        }
        System.out.println("-".repeat(len));
    }

    private static void rule(int len) {
        System.out.println("-".repeat(Math.max(10, len)));
    }

    private static void kv(String label, Object value) {
        System.out.printf("  %-18s %s%n", label + ":", value == null ? "—" : value);
    }

    /** Pad or truncate to exact display width. */
    private static String fit(String s, int width) {
        if (s == null) {
            s = "";
        }
        if (s.length() <= width) {
            return String.format("%-" + width + "s", s);
        }
        if (width <= 1) {
            return s.substring(0, width);
        }
        return s.substring(0, width - 1) + "…";
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
