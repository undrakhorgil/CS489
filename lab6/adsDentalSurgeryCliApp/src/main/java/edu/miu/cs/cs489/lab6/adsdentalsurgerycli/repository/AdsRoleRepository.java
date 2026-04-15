package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.repository;

import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.AdsRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdsRoleRepository extends JpaRepository<AdsRole, Long> {

    Optional<AdsRole> findByName(String name);
}
