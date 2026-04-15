package edu.miu.cs.cs489.lab7b.adsgraphql.repository;

import edu.miu.cs.cs489.lab7b.adsgraphql.model.Surgery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurgeryRepository extends JpaRepository<Surgery, Long> {
}

