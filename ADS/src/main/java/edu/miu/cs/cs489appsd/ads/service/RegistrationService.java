package edu.miu.cs.cs489appsd.ads.service;

import edu.miu.cs.cs489appsd.ads.domain.Dentist;
import edu.miu.cs.cs489appsd.ads.domain.Patient;
import edu.miu.cs.cs489appsd.ads.domain.Surgery;
import edu.miu.cs.cs489appsd.ads.repository.DentistRepository;
import edu.miu.cs.cs489appsd.ads.repository.PatientRepository;
import edu.miu.cs.cs489appsd.ads.repository.SurgeryRepository;
import edu.miu.cs.cs489appsd.ads.web.dto.DentistRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.DentistResponse;
import edu.miu.cs.cs489appsd.ads.web.dto.PatientRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.PatientResponse;
import edu.miu.cs.cs489appsd.ads.web.dto.SurgeryRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.SurgeryResponse;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final DentistRepository dentistRepository;
    private final PatientRepository patientRepository;
    private final SurgeryRepository surgeryRepository;

    public RegistrationService(DentistRepository dentistRepository,
                               PatientRepository patientRepository,
                               SurgeryRepository surgeryRepository) {
        this.dentistRepository = dentistRepository;
        this.patientRepository = patientRepository;
        this.surgeryRepository = surgeryRepository;
    }

    public DentistResponse registerDentist(DentistRequest req) {
        Dentist d = new Dentist();
        d.setFirstName(req.firstName());
        d.setLastName(req.lastName());
        d.setContactPhoneNumber(req.contactPhoneNumber());
        d.setEmail(req.email());
        d.setSpecialization(req.specialization());
        Dentist saved = dentistRepository.save(d);
        return toDentistResponse(saved);
    }

    public PatientResponse enrollPatient(PatientRequest req) {
        Patient p = new Patient();
        p.setFirstName(req.firstName());
        p.setLastName(req.lastName());
        p.setContactPhoneNumber(req.contactPhoneNumber());
        p.setEmail(req.email());
        p.setMailingAddress(req.mailingAddress());
        p.setDateOfBirth(req.dateOfBirth());
        Patient saved = patientRepository.save(p);
        return toPatientResponse(saved);
    }

    public SurgeryResponse registerSurgery(SurgeryRequest req) {
        Surgery s = new Surgery();
        s.setName(req.name());
        s.setLocationAddress(req.locationAddress());
        s.setTelephoneNumber(req.telephoneNumber());
        Surgery saved = surgeryRepository.save(s);
        return toSurgeryResponse(saved);
    }

    private static DentistResponse toDentistResponse(Dentist d) {
        return new DentistResponse(
                d.getDentistId(),
                d.getFirstName(),
                d.getLastName(),
                d.getContactPhoneNumber(),
                d.getEmail(),
                d.getSpecialization()
        );
    }

    private static PatientResponse toPatientResponse(Patient p) {
        return new PatientResponse(
                p.getPatientId(),
                p.getFirstName(),
                p.getLastName(),
                p.getContactPhoneNumber(),
                p.getEmail(),
                p.getMailingAddress(),
                p.getDateOfBirth()
        );
    }

    private static SurgeryResponse toSurgeryResponse(Surgery s) {
        return new SurgeryResponse(
                s.getSurgeryId(),
                s.getName(),
                s.getLocationAddress(),
                s.getTelephoneNumber()
        );
    }
}
