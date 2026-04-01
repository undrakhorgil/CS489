package edu.miu.cs.cs489appsd.ads.repository;

import edu.miu.cs.cs489appsd.ads.domain.Appointment;
import edu.miu.cs.cs489appsd.ads.domain.AppointmentStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryAppointmentRepository implements AppointmentRepository {

    private final Map<Long, Appointment> store = new ConcurrentHashMap<>();
    private final AtomicLong id = new AtomicLong(1);

    @Override
    public synchronized Appointment save(Appointment appointment) {
        if (appointment.getAppointmentId() == null) {
            appointment.setAppointmentId(id.getAndIncrement());
        }
        store.put(appointment.getAppointmentId(), appointment);
        return appointment;
    }

    @Override
    public Optional<Appointment> findById(Long appointmentId) {
        return Optional.ofNullable(store.get(appointmentId));
    }

    @Override
    public List<Appointment> findByDentistId(Long dentistId) {
        return store.values().stream()
                .filter(a -> dentistId.equals(a.getDentistId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Appointment> findByPatientId(Long patientId) {
        return store.values().stream()
                .filter(a -> patientId.equals(a.getPatientId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Appointment> findByDentistIdAndStatus(Long dentistId, AppointmentStatus status) {
        return store.values().stream()
                .filter(a -> dentistId.equals(a.getDentistId()) && status == a.getStatus())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Appointment> findAll() {
        return new ArrayList<>(store.values());
    }
}
