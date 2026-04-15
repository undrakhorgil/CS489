package edu.miu.cs.cs489.lab7b.welcome.repository;

import edu.miu.cs.cs489.lab7b.welcome.domain.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DentistRepository extends JpaRepository<Dentist, Long> {
}

