package edu.miu.cs.cs489appsd.ads.repository;

import edu.miu.cs.cs489appsd.ads.domain.Appointment;
import edu.miu.cs.cs489appsd.ads.domain.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDentistId(Long dentistId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDentistIdAndStatus(Long dentistId, AppointmentStatus status);
}
