package edu.miu.cs.cs489appsd.ads.api.dto.mapper;

import edu.miu.cs.cs489appsd.ads.domain.Dentist;
import edu.miu.cs.cs489appsd.ads.domain.Patient;
import edu.miu.cs.cs489appsd.ads.domain.Surgery;
import edu.miu.cs.cs489appsd.ads.api.dto.DentistResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.PatientResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.SurgeryResponse;

/**
 * Maps domain entities to API response DTOs.
 */
public final class ResponseDtoMapper {

    private ResponseDtoMapper() {
    }

    public static DentistResponse toDentistResponse(Dentist d) {
        return new DentistResponse(
                d.getDentistId(),
                d.getFirstName(),
                d.getLastName(),
                d.getContactPhoneNumber(),
                d.getEmail(),
                d.getSpecialization()
        );
    }

    public static PatientResponse toPatientResponse(Patient p) {
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

    public static SurgeryResponse toSurgeryResponse(Surgery s) {
        return new SurgeryResponse(
                s.getSurgeryId(),
                s.getName(),
                s.getLocationAddress(),
                s.getTelephoneNumber()
        );
    }
}
