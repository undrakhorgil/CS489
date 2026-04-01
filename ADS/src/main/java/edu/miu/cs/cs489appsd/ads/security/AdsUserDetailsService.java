package edu.miu.cs.cs489appsd.ads.security;

import edu.miu.cs.cs489appsd.ads.domain.Account;
import edu.miu.cs.cs489appsd.ads.repository.AccountRepository;
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
        Account a = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new AdsUserDetails(
                a.getUsername(),
                a.getPasswordHash(),
                a.getRole(),
                a.getDentistId(),
                a.getPatientId()
        );
    }
}
