package edu.miu.cs.cs489appsd.ads.repository;

import edu.miu.cs.cs489appsd.ads.domain.Appointment;
import edu.miu.cs.cs489appsd.ads.domain.AppointmentStatus;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {

    Appointment save(Appointment appointment);

    Optional<Appointment> findById(Long appointmentId);

    List<Appointment> findByDentistId(Long dentistId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDentistIdAndStatus(Long dentistId, AppointmentStatus status);

    List<Appointment> findAll();
}
