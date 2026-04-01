package edu.miu.cs.cs489appsd.ads.repository;

import edu.miu.cs.cs489appsd.ads.domain.Bill;

import java.util.List;
import java.util.Optional;

public interface BillRepository {

    Bill save(Bill bill);

    Optional<Bill> findById(Long billId);

    List<Bill> findByPatientId(Long patientId);

    boolean existsUnpaidByPatientId(Long patientId);
}
