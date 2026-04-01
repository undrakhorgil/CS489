package edu.miu.cs.cs489appsd.ads.domain;

import java.time.LocalDateTime;

public class Appointment {
    private Long appointmentId;
    private Long patientId;
    private Long dentistId;
    private Long surgeryId;
    private LocalDateTime startAt;
    /** When patient requests reschedule, proposed slot before office confirms */
    private LocalDateTime proposedStartAt;
    private AppointmentStatus status;
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
