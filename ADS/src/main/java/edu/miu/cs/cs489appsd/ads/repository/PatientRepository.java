package edu.miu.cs.cs489appsd.ads.repository;

import edu.miu.cs.cs489appsd.ads.domain.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {

    Patient save(Patient patient);

    Optional<Patient> findById(Long patientId);

    List<Patient> findAll();
}
