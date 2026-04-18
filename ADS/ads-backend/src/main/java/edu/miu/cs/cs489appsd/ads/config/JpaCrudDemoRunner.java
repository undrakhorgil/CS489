package edu.miu.cs.cs489appsd.ads.config;

import edu.miu.cs.cs489appsd.ads.domain.Surgery;
import edu.miu.cs.cs489appsd.ads.repository.SurgeryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Optional JPA CRUD demo against {@code surgeries}. Enable with {@code --spring.profiles.active=demo-jpa}.
 */
@Component
@Order(Integer.MAX_VALUE)
@Profile("demo-jpa")
public class JpaCrudDemoRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(JpaCrudDemoRunner.class);

    private final SurgeryRepository surgeryRepository;

    public JpaCrudDemoRunner(SurgeryRepository surgeryRepository) {
        this.surgeryRepository = surgeryRepository;
    }

    @Override
    public void run(String... args) {
        log.info("--- JPA CRUD demo (Surgery) ---");
        Surgery created = surgeryRepository.save(new Surgery(null, "ADS - Demo Site",
                "1 Demo Lane, Fairfield, IA", "555-0199"));
        log.info("CREATE: {}", created.getSurgeryId());

        Surgery read = surgeryRepository.findById(created.getSurgeryId()).orElseThrow();
        log.info("READ:   {}", read.getName());

        read.setTelephoneNumber("555-0200");
        Surgery updated = surgeryRepository.save(read);
        log.info("UPDATE: {}", updated.getTelephoneNumber());

        surgeryRepository.deleteById(created.getSurgeryId());
        log.info("DELETE: removed surgery id {}", created.getSurgeryId());
    }
}
