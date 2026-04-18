package edu.miu.cs.cs489appsd.ads.service;

import edu.miu.cs.cs489appsd.ads.domain.Bill;
import edu.miu.cs.cs489appsd.ads.exception.NotFoundException;
import edu.miu.cs.cs489appsd.ads.repository.BillRepository;
import edu.miu.cs.cs489appsd.ads.repository.PatientRepository;
import edu.miu.cs.cs489appsd.ads.api.dto.BillRequest;
import org.springframework.stereotype.Service;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final PatientRepository patientRepository;

    public BillService(BillRepository billRepository, PatientRepository patientRepository) {
        this.billRepository = billRepository;
        this.patientRepository = patientRepository;
    }

    public Bill recordBill(BillRequest req) {
        patientRepository.findById(req.patientId())
                .orElseThrow(() -> new NotFoundException("Patient not found: " + req.patientId()));
        Bill b = new Bill();
        b.setPatientId(req.patientId());
        b.setAmount(req.amount());
        b.setDueDate(req.dueDate());
        b.setPaid(req.paid());
        return billRepository.save(b);
    }
}
