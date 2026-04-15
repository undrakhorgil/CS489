package edu.miu.cs.cs489.lab7b.adsgraphql.repository;

import edu.miu.cs.cs489.lab7b.adsgraphql.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}

