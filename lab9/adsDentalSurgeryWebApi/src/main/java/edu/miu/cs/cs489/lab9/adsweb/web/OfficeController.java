package edu.miu.cs.cs489.lab9.adsweb.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/office")
public class OfficeController {
    @GetMapping("/dashboard")
    public Map<String, String> dashboard() {
        return Map.of("message", "Office Manager dashboard (protected)");
    }
}

