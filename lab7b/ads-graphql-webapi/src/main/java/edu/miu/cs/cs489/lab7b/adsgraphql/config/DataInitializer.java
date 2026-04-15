package edu.miu.cs.cs489.lab7b.adsgraphql.config;

import edu.miu.cs.cs489.lab7b.adsgraphql.model.Appointment;
import edu.miu.cs.cs489.lab7b.adsgraphql.model.AppointmentRequestChannel;
import edu.miu.cs.cs489.lab7b.adsgraphql.model.AppointmentStatus;
import edu.miu.cs.cs489.lab7b.adsgraphql.model.Bill;
import edu.miu.cs.cs489.lab7b.adsgraphql.model.Dentist;
import edu.miu.cs.cs489.lab7b.adsgraphql.model.Patient;
import edu.miu.cs.cs489.lab7b.adsgraphql.model.Surgery;
import edu.miu.cs.cs489.lab7b.adsgraphql.repository.AppointmentRepository;
import edu.miu.cs.cs489.lab7b.adsgraphql.repository.BillRepository;
import edu.miu.cs.cs489.lab7b.adsgraphql.repository.DentistRepository;
import edu.miu.cs.cs489.lab7b.adsgraphql.repository.PatientRepository;
import edu.miu.cs.cs489.lab7b.adsgraphql.repository.SurgeryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedData(PatientRepository patientRepository,
                                      DentistRepository dentistRepository,
                                      SurgeryRepository surgeryRepository,
                                      AppointmentRepository appointmentRepository,
                                      BillRepository billRepository) {
        return args -> {
            if (patientRepository.count() > 0) {
                return;
            }

            Patient p1 = new Patient();
            p1.setFirstName("Alice");
            p1.setLastName("Johnson");
            p1.setContactPhoneNumber("515-555-0101");
            p1.setEmail("alice@example.com");
            p1.setMailingAddress("1000 N 4th St, Fairfield, IA");
            p1.setDateOfBirth(LocalDate.of(1995, 2, 12));
            p1 = patientRepository.save(p1);

            Dentist d1 = new Dentist();
            d1.setFirstName("Bob");
            d1.setLastName("Smith");
            d1.setContactPhoneNumber("515-555-0202");
            d1.setEmail("bob.smith@example.com");
            d1.setSpecialization("General Dentistry");
            d1 = dentistRepository.save(d1);

            Surgery s1 = new Surgery();
            s1.setName("Advantis Fairfield");
            s1.setLocationAddress("2000 W Burlington Ave, Fairfield, IA");
            s1.setTelephoneNumber("515-555-0303");
            s1 = surgeryRepository.save(s1);

            Appointment a1 = new Appointment();
            a1.setPatient(p1);
            a1.setDentist(d1);
            a1.setSurgery(s1);
            a1.setStartAt(LocalDateTime.now().plusDays(2).withSecond(0).withNano(0));
            a1.setStatus(AppointmentStatus.REQUESTED);
            a1.setChannel(AppointmentRequestChannel.WEB);
            a1 = appointmentRepository.save(a1);

            Bill b1 = new Bill();
            b1.setPatient(p1);
            b1.setAmount(new BigDecimal("120.00"));
            b1.setDueDate(LocalDate.now().plusDays(30));
            b1.setPaid(false);
            billRepository.save(b1);
        };
    }
}

