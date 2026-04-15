package edu.miu.cs.cs489.lab7b.adsgraphql.repository;

import edu.miu.cs.cs489.lab7b.adsgraphql.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient_PatientId(Long patientId);
    List<Appointment> findByDentist_DentistId(Long dentistId);
}

