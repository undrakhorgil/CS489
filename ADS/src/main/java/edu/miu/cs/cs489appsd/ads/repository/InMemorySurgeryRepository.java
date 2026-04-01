package edu.miu.cs.cs489appsd.ads.repository;

import edu.miu.cs.cs489appsd.ads.domain.Surgery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemorySurgeryRepository implements SurgeryRepository {

    private final Map<Long, Surgery> store = new ConcurrentHashMap<>();
    private final AtomicLong id = new AtomicLong(1);

    @Override
    public synchronized Surgery save(Surgery surgery) {
        if (surgery.getSurgeryId() == null) {
            surgery.setSurgeryId(id.getAndIncrement());
        }
        store.put(surgery.getSurgeryId(), surgery);
        return surgery;
    }

    @Override
    public Optional<Surgery> findById(Long surgeryId) {
        return Optional.ofNullable(store.get(surgeryId));
    }

    @Override
    public List<Surgery> findAll() {
        return new ArrayList<>(store.values());
    }
}
