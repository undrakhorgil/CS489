package edu.miu.cs.cs489appsd.ads.repository;

import edu.miu.cs.cs489appsd.ads.domain.Surgery;

import java.util.List;
import java.util.Optional;

public interface SurgeryRepository {

    Surgery save(Surgery surgery);

    Optional<Surgery> findById(Long surgeryId);

    List<Surgery> findAll();
}
