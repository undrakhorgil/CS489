package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.service.impl;

import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.Dentist;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.repository.DentistRepository;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.service.DentistService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DentistServiceImpl implements DentistService {

    private final DentistRepository dentistRepository;

    public DentistServiceImpl(DentistRepository dentistRepository) {
        this.dentistRepository = dentistRepository;
    }

    @Override
    public List<Dentist> findAllSortedByLastName() {
        return dentistRepository.findAllByOrderByLastNameAscFirstNameAsc();
    }

    @Override
    public Dentist save(Dentist dentist) {
        return dentistRepository.save(dentist);
    }

    @Override
    public Optional<Dentist> findById(Long id) {
        return dentistRepository.findById(id);
    }

    @Override
    public Dentist update(Dentist dentist) {
        return dentistRepository.save(dentist);
    }

    @Override
    public void deleteById(Long id) {
        dentistRepository.deleteById(id);
    }

    @Override
    public Optional<Dentist> findByEmail(String email) {
        return dentistRepository.findByEmail(email);
    }
}
