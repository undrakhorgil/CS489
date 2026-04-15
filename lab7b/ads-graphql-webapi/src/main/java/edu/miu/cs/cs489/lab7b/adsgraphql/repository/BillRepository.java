package edu.miu.cs.cs489.lab7b.adsgraphql.repository;

import edu.miu.cs.cs489.lab7b.adsgraphql.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByPatient_PatientId(Long patientId);
}

