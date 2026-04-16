package edu.miu.cs.cs489.lab7.adsweb.controller;

import edu.miu.cs.cs489.lab7.adsweb.dto.patient.PatientRequest;
import edu.miu.cs.cs489.lab7.adsweb.dto.patient.PatientResponse;
import edu.miu.cs.cs489.lab7.adsweb.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adsweb/api/v1")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PatientResponse>> listPatients() {
        return ResponseEntity.ok(patientService.getAllPatientsSortedByLastName());
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getPatientById(patientId));
    }

    @PostMapping("/patients")
    public ResponseEntity<PatientResponse> registerPatient(@Valid @RequestBody PatientRequest request) {
        return new ResponseEntity<>(patientService.registerPatient(request), HttpStatus.CREATED);
    }

    @PutMapping("/patient/{patientId}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long patientId, @Valid @RequestBody PatientRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(patientId, request));
    }

    @DeleteMapping("/patient/{patientId}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long patientId) {
        patientService.deletePatientById(patientId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/patient/search/{searchString}")
    public ResponseEntity<List<PatientResponse>> searchPatients(@PathVariable String searchString) {
        return ResponseEntity.ok(patientService.searchPatients(searchString));
    }
}
