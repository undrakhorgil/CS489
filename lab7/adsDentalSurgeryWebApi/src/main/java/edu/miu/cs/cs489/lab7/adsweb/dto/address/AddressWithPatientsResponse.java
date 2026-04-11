package edu.miu.cs.cs489.lab7.adsweb.dto.address;

import edu.miu.cs.cs489.lab7.adsweb.dto.patient.PatientResponse;

import java.util.List;

public record AddressWithPatientsResponse(
        Long addressId,
        String street,
        String city,
        String state,
        String zipCode,
        List<PatientResponse> patients
) {
}
