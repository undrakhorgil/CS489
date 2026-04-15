package edu.miu.cs.cs489.lab7b.welcome.repository;

import edu.miu.cs.cs489.lab7b.welcome.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}

