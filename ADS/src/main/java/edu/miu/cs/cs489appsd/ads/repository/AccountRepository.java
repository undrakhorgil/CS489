package edu.miu.cs.cs489appsd.ads.repository;

import edu.miu.cs.cs489appsd.ads.domain.Account;

import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findByUsername(String username);
}
