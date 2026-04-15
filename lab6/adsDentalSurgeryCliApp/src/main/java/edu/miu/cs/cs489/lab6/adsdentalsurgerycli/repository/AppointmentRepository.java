package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.repository;

import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.Appointment;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDentist_DentistIdOrderByStartAtAsc(Long dentistId);

    List<Appointment> findByStatusOrderByStartAtAsc(AppointmentStatus status);
}
