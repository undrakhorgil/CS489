package edu.miu.cs.cs489.lab7.adsweb.service.impl;

import edu.miu.cs.cs489.lab7.adsweb.dto.address.AddressRequest;
import edu.miu.cs.cs489.lab7.adsweb.dto.patient.PatientRequest;
import edu.miu.cs.cs489.lab7.adsweb.dto.patient.PatientResponse;
import edu.miu.cs.cs489.lab7.adsweb.exception.PatientNotFoundException;
import edu.miu.cs.cs489.lab7.adsweb.model.Address;
import edu.miu.cs.cs489.lab7.adsweb.model.Patient;
import edu.miu.cs.cs489.lab7.adsweb.repository.PatientRepository;
import edu.miu.cs.cs489.lab7.adsweb.service.PatientService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatientsSortedByLastName() {
        return patientRepository.findAll(Sort.by("lastName", "firstName")).stream()
                .map(PatientResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient with id " + patientId + " not found"));
        return PatientResponse.fromEntity(patient);
    }

    @Override
    @Transactional
    public PatientResponse registerPatient(PatientRequest request) {
        Patient patient = new Patient();
        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setContactPhoneNumber(request.contactPhoneNumber());
        patient.setEmail(request.email());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setPatientRef(blankToNull(request.patientRef()));
        patient.setMailingAddress(fromAddressRequest(request.primaryAddress()));
        Patient saved = patientRepository.save(patient);
        return PatientResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public PatientResponse updatePatient(Long patientId, PatientRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient with id " + patientId + " not found"));
        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setContactPhoneNumber(request.contactPhoneNumber());
        patient.setEmail(request.email());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setPatientRef(blankToNull(request.patientRef()));
        applyAddressUpdate(patient, request.primaryAddress());
        return PatientResponse.fromEntity(patientRepository.save(patient));
    }

    @Override
    @Transactional
    public void deletePatientById(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException("Patient with id " + patientId + " not found");
        }
        patientRepository.deleteById(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> searchPatients(String searchString) {
        if (searchString == null || searchString.isBlank()) {
            return List.of();
        }
        String q = searchString.trim();
        return patientRepository.search(q).stream()
                .sorted(Comparator.comparing(Patient::getLastName, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(Patient::getFirstName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(PatientResponse::fromEntity)
                .toList();
    }

    private static void applyAddressUpdate(Patient patient, AddressRequest req) {
        Address addr = patient.getMailingAddress();
        if (addr == null) {
            patient.setMailingAddress(fromAddressRequest(req));
            return;
        }
        addr.setStreet(req.street());
        addr.setCity(req.city());
        addr.setState(req.state());
        addr.setZipCode(blankToNull(req.zipCode()));
    }

    private static Address fromAddressRequest(AddressRequest req) {
        return new Address(
                null,
                req.street(),
                req.city(),
                req.state(),
                blankToNull(req.zipCode()),
                new java.util.ArrayList<>());
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

}
