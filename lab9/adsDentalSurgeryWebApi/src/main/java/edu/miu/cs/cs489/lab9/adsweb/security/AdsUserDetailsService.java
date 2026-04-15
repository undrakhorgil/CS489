package edu.miu.cs.cs489.lab9.adsweb.security;

import edu.miu.cs.cs489.lab9.adsweb.domain.Account;
import edu.miu.cs.cs489.lab9.adsweb.repository.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdsUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    public AdsUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account a = accountRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new AdsUserDetails(a.getUsername(), a.getPasswordHash(), a.getRole());
    }
}

