package edu.miu.cs.cs489appsd.ads.service;

import edu.miu.cs.cs489appsd.ads.domain.Dentist;
import edu.miu.cs.cs489appsd.ads.domain.Patient;
import edu.miu.cs.cs489appsd.ads.domain.Surgery;
import edu.miu.cs.cs489appsd.ads.repository.DentistRepository;
import edu.miu.cs.cs489appsd.ads.repository.PatientRepository;
import edu.miu.cs.cs489appsd.ads.repository.SurgeryRepository;
import edu.miu.cs.cs489appsd.ads.api.dto.DentistRequest;
import edu.miu.cs.cs489appsd.ads.api.dto.DentistResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.PatientRequest;
import edu.miu.cs.cs489appsd.ads.api.dto.PatientResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.SurgeryRequest;
import edu.miu.cs.cs489appsd.ads.api.dto.SurgeryResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.mapper.ResponseDtoMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

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
        return ResponseDtoMapper.toDentistResponse(saved);
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
        return ResponseDtoMapper.toPatientResponse(saved);
    }

    public SurgeryResponse registerSurgery(SurgeryRequest req) {
        Surgery s = new Surgery();
        s.setName(req.name());
        s.setLocationAddress(req.locationAddress());
        s.setTelephoneNumber(req.telephoneNumber());
        Surgery saved = surgeryRepository.save(s);
        return ResponseDtoMapper.toSurgeryResponse(saved);
    }

    public List<PatientResponse> listAllPatients() {
        return patientRepository.findAll().stream()
                .sorted(Comparator.comparing(Patient::getLastName).thenComparing(Patient::getFirstName))
                .map(ResponseDtoMapper::toPatientResponse)
                .toList();
    }

    public List<DentistResponse> listAllDentists() {
        return dentistRepository.findAll().stream()
                .sorted(Comparator.comparing(Dentist::getLastName).thenComparing(Dentist::getFirstName))
                .map(ResponseDtoMapper::toDentistResponse)
                .toList();
    }

    public List<SurgeryResponse> listAllSurgeries() {
        return surgeryRepository.findAll().stream()
                .sorted(Comparator.comparing(Surgery::getName))
                .map(ResponseDtoMapper::toSurgeryResponse)
                .toList();
    }
}
