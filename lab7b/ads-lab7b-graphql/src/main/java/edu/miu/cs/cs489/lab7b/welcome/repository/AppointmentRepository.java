package edu.miu.cs.cs489.lab7b.welcome.repository;

import edu.miu.cs.cs489.lab7b.welcome.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDentistId(Long dentistId);
}

