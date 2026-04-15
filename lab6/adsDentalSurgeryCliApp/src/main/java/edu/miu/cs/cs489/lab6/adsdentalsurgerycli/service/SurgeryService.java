package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.service;

import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.Surgery;

import java.util.List;
import java.util.Optional;

public interface SurgeryService {

    List<Surgery> findAll();

    Surgery save(Surgery surgery);

    Optional<Surgery> findById(Long id);

    Surgery update(Surgery surgery);

    void deleteById(Long id);
}
