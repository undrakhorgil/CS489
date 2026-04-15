package edu.miu.cs.cs489.lab7b.welcome.repository;

import edu.miu.cs.cs489.lab7b.welcome.domain.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByPatientId(Long patientId);
}

