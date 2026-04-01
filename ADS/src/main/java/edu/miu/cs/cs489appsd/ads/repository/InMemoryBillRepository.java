package edu.miu.cs.cs489appsd.ads.repository;

import edu.miu.cs.cs489appsd.ads.domain.Bill;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryBillRepository implements BillRepository {

    private final Map<Long, Bill> store = new ConcurrentHashMap<>();
    private final AtomicLong id = new AtomicLong(1);

    @Override
    public synchronized Bill save(Bill bill) {
        if (bill.getBillId() == null) {
            bill.setBillId(id.getAndIncrement());
        }
        store.put(bill.getBillId(), bill);
        return bill;
    }

    @Override
    public Optional<Bill> findById(Long billId) {
        return Optional.ofNullable(store.get(billId));
    }

    @Override
    public List<Bill> findByPatientId(Long patientId) {
        return store.values().stream()
                .filter(b -> patientId.equals(b.getPatientId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean existsUnpaidByPatientId(Long patientId) {
        return store.values().stream()
                .anyMatch(b -> patientId.equals(b.getPatientId()) && !b.isPaid());
    }
}
