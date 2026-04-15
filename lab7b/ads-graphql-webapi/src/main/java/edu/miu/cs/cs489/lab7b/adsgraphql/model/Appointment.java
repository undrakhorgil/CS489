package edu.miu.cs.cs489.lab7b.adsgraphql.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dentist_id")
    private Dentist dentist;

    @ManyToOne(optional = false)
    @JoinColumn(name = "surgery_id")
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
    private AppointmentRequestChannel channel;

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Dentist getDentist() {
        return dentist;
    }

    public void setDentist(Dentist dentist) {
        this.dentist = dentist;
    }

    public Surgery getSurgery() {
        return surgery;
    }

    public void setSurgery(Surgery surgery) {
        this.surgery = surgery;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getProposedStartAt() {
        return proposedStartAt;
    }

    public void setProposedStartAt(LocalDateTime proposedStartAt) {
        this.proposedStartAt = proposedStartAt;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public AppointmentRequestChannel getChannel() {
        return channel;
    }

    public void setChannel(AppointmentRequestChannel channel) {
        this.channel = channel;
    }
}

