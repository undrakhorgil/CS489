package edu.miu.cs.cs489.lab7b.welcome.repository;

import edu.miu.cs.cs489.lab7b.welcome.domain.Surgery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurgeryRepository extends JpaRepository<Surgery, Long> {
}

