package edu.miu.cs.cs489appsd.ads.repository;

import edu.miu.cs.cs489appsd.ads.domain.Dentist;

import java.util.List;
import java.util.Optional;

public interface DentistRepository {

    Dentist save(Dentist dentist);

    Optional<Dentist> findById(Long dentistId);

    List<Dentist> findAll();
}
