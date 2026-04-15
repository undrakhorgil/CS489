package edu.miu.cs.cs489.lab9.adsweb.config;

import edu.miu.cs.cs489.lab9.adsweb.domain.Account;
import edu.miu.cs.cs489.lab9.adsweb.domain.Role;
import edu.miu.cs.cs489.lab9.adsweb.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseSeeder {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void seed() {
        if (accountRepository.findByUsernameIgnoreCase("manager").isPresent()) {
            return;
        }

        accountRepository.save(new Account(null, "manager", passwordEncoder.encode("password"), Role.OFFICE_MANAGER));
        accountRepository.save(new Account(null, "dentist1", passwordEncoder.encode("password"), Role.DENTIST));
        accountRepository.save(new Account(null, "patient1", passwordEncoder.encode("password"), Role.PATIENT));
    }
}

