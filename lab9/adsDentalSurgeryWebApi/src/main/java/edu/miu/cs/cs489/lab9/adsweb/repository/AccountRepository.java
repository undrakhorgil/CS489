package edu.miu.cs.cs489.lab9.adsweb.repository;

import edu.miu.cs.cs489.lab9.adsweb.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsernameIgnoreCase(String username);
}

