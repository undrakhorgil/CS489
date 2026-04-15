package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.service.impl;

import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.Surgery;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.repository.SurgeryRepository;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.service.SurgeryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SurgeryServiceImpl implements SurgeryService {

    private final SurgeryRepository surgeryRepository;

    public SurgeryServiceImpl(SurgeryRepository surgeryRepository) {
        this.surgeryRepository = surgeryRepository;
    }

    @Override
    public List<Surgery> findAll() {
        return surgeryRepository.findAll();
    }

    @Override
    public Surgery save(Surgery surgery) {
        return surgeryRepository.save(surgery);
    }

    @Override
    public Optional<Surgery> findById(Long id) {
        return surgeryRepository.findById(id);
    }

    @Override
    public Surgery update(Surgery surgery) {
        return surgeryRepository.save(surgery);
    }

    @Override
    public void deleteById(Long id) {
        surgeryRepository.deleteById(id);
    }
}
