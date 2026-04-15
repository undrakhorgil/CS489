package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.bootstrap;

import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Existing {@code users} rows (e.g. from an older schema without {@code password}) get NULL for the new
 * column; Hibernate cannot add {@code NOT NULL} in one step. We add a nullable column, then set a demo
 * password here so the row is usable with {@link SampleDataLoader}'s convention.
 */
@Component
@Profile("!test")
@Order(0)
public class UsersPasswordBackfill implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(UsersPasswordBackfill.class);

    private static final String DEMO_PASSWORD = "password";

    private final AppUserRepository appUserRepository;

    public UsersPasswordBackfill(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var missing = appUserRepository.findUsersWithMissingPassword();
        if (missing.isEmpty()) {
            return;
        }
        missing.forEach(u -> u.setPassword(DEMO_PASSWORD));
        appUserRepository.saveAll(missing);
        log.info("Backfilled password for {} legacy user row(s) (demo password: {}).", missing.size(), DEMO_PASSWORD);
    }
}
