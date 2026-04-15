package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.repository;

import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DentistRepository extends JpaRepository<Dentist, Long> {

    Optional<Dentist> findByEmail(String email);

    List<Dentist> findAllByOrderByLastNameAscFirstNameAsc();
}
