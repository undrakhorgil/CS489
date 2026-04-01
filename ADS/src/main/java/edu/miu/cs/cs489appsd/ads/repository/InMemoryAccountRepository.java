package edu.miu.cs.cs489appsd.ads.repository;

import edu.miu.cs.cs489appsd.ads.domain.Account;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> byUsername = new ConcurrentHashMap<>();
    private final AtomicLong id = new AtomicLong(1);

    @Override
    public synchronized Account save(Account account) {
        if (account.getAccountId() == null) {
            account.setAccountId(id.getAndIncrement());
        }
        byUsername.put(account.getUsername().toLowerCase(), account);
        return account;
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        return Optional.ofNullable(byUsername.get(username.toLowerCase()));
    }
}
