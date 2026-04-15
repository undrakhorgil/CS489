package edu.miu.cs.cs489.lab7.adsweb.service.impl;

import edu.miu.cs.cs489.lab7.adsweb.dto.address.AddressWithPatientsResponse;
import edu.miu.cs.cs489.lab7.adsweb.dto.patient.PatientResponse;
import edu.miu.cs.cs489.lab7.adsweb.model.Address;
import edu.miu.cs.cs489.lab7.adsweb.model.Patient;
import edu.miu.cs.cs489.lab7.adsweb.repository.AddressRepository;
import edu.miu.cs.cs489.lab7.adsweb.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressWithPatientsResponse> getAllAddressesSortedByCityWithPatients() {
        List<Address> rows = addressRepository.findAllWithPatientsOrderByCityAsc();
        return rows.stream().map(AddressServiceImpl::toResponse).toList();
    }

    private static AddressWithPatientsResponse toResponse(Address a) {
        List<Patient> plist = a.getPatients() == null ? List.of() : a.getPatients();
        List<PatientResponse> patients = plist.stream()
                .sorted(Comparator.comparing(Patient::getLastName, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(Patient::getFirstName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(PatientResponse::fromEntity)
                .toList();
        return new AddressWithPatientsResponse(
                a.getAddressId(),
                a.getStreet(),
                a.getCity(),
                a.getState(),
                a.getZipCode(),
                patients);
    }
}
