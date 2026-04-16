package edu.miu.cs.cs489.lab7.adsweb.dto.patient;

import edu.miu.cs.cs489.lab7.adsweb.dto.address.AddressResponse;
import edu.miu.cs.cs489.lab7.adsweb.model.Patient;

import java.time.LocalDate;

public record PatientResponse(
        Long patientId,
        String firstName,
        String lastName,
        String contactPhoneNumber,
        String email,
        LocalDate dateOfBirth,
        String patientRef,
        AddressResponse primaryAddress
) {
    public static PatientResponse fromEntity(Patient p) {
        return new PatientResponse(
                p.getPatientId(),
                p.getFirstName(),
                p.getLastName(),
                p.getContactPhoneNumber(),
                p.getEmail(),
                p.getDateOfBirth(),
                p.getPatientRef(),
                AddressResponse.fromEntity(p.getMailingAddress()));
    }
}
