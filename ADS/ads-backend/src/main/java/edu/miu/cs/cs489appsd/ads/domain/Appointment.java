package edu.miu.cs.cs489appsd.ads.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "dentist_id", nullable = false)
    private Long dentistId;

    @Column(name = "surgery_id", nullable = false)
    private Long surgeryId;

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

    public Appointment() {
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(Long dentistId) {
        this.dentistId = dentistId;
    }

    public Long getSurgeryId() {
        return surgeryId;
    }

    public void setSurgeryId(Long surgeryId) {
        this.surgeryId = surgeryId;
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
