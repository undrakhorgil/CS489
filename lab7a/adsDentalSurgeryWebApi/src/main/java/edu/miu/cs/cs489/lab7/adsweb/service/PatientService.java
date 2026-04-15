package edu.miu.cs.cs489.lab7.adsweb.service;

import edu.miu.cs.cs489.lab7.adsweb.dto.patient.PatientRequest;
import edu.miu.cs.cs489.lab7.adsweb.dto.patient.PatientResponse;

import java.util.List;

public interface PatientService {

    List<PatientResponse> getAllPatientsSortedByLastName();

    PatientResponse getPatientById(Long patientId);

    PatientResponse registerPatient(PatientRequest request);

    PatientResponse updatePatient(Long patientId, PatientRequest request);

    void deletePatientById(Long patientId);

    List<PatientResponse> searchPatients(String searchString);
}
