package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "dentist_id", nullable = false)
    private Dentist dentist;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "surgery_id", nullable = false)
    private Surgery surgery;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "proposed_start_at")
    private LocalDateTime proposedStartAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AppointmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AppointmentChannel channel;

    @Override
    public String toString() {
        return "Appointment{" + "appointmentId=" + appointmentId + ", startAt=" + startAt + ", status="
                + status + ", channel=" + channel + '}';
    }
}
