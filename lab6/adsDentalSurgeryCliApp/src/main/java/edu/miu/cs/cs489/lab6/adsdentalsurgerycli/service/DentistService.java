package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.service;

import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.Dentist;

import java.util.List;
import java.util.Optional;

public interface DentistService {

    List<Dentist> findAllSortedByLastName();

    Dentist save(Dentist dentist);

    Optional<Dentist> findById(Long id);

    Dentist update(Dentist dentist);

    void deleteById(Long id);

    Optional<Dentist> findByEmail(String email);
}
