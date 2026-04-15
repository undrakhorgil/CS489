package edu.miu.cs.cs489.lab9.adsweb.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/patient")
public class PatientController {
    @GetMapping("/appointments")
    public Map<String, String> myAppointments() {
        return Map.of("message", "Patient appointments (protected)");
    }
}

