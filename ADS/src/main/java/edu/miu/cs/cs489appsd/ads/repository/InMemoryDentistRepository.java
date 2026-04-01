package edu.miu.cs.cs489appsd.ads.repository;

import edu.miu.cs.cs489appsd.ads.domain.Dentist;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryDentistRepository implements DentistRepository {

    private final Map<Long, Dentist> store = new ConcurrentHashMap<>();
    private final AtomicLong id = new AtomicLong(1);

    @Override
    public synchronized Dentist save(Dentist dentist) {
        if (dentist.getDentistId() == null) {
            dentist.setDentistId(id.getAndIncrement());
        }
        store.put(dentist.getDentistId(), dentist);
        return dentist;
    }

    @Override
    public Optional<Dentist> findById(Long dentistId) {
        return Optional.ofNullable(store.get(dentistId));
    }

    @Override
    public List<Dentist> findAll() {
        return new ArrayList<>(store.values());
    }
}
