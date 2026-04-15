package edu.miu.cs.cs489.lab7b.adsgraphql.repository;

import edu.miu.cs.cs489.lab7b.adsgraphql.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DentistRepository extends JpaRepository<Dentist, Long> {
}

