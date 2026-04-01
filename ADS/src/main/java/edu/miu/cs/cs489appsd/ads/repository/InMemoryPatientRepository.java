package edu.miu.cs.cs489appsd.ads.repository;

import edu.miu.cs.cs489appsd.ads.domain.Patient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryPatientRepository implements PatientRepository {

    private final Map<Long, Patient> store = new ConcurrentHashMap<>();
    private final AtomicLong id = new AtomicLong(1);

    @Override
    public synchronized Patient save(Patient patient) {
        if (patient.getPatientId() == null) {
            patient.setPatientId(id.getAndIncrement());
        }
        store.put(patient.getPatientId(), patient);
        return patient;
    }

    @Override
    public Optional<Patient> findById(Long patientId) {
        return Optional.ofNullable(store.get(patientId));
    }

    @Override
    public List<Patient> findAll() {
        return new ArrayList<>(store.values());
    }
}
