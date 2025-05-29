package com.smartclinic.controller;

import com.smartclinic.model.Login;
import com.smartclinic.model.Patient;
import com.smartclinic.service.PatientService;
import com.smartclinic.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    @Autowired
    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    // 1. Get Patient Details by Token
    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatientDetails(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode())
                    .body(Map.of("error", "Unauthorized access"));
        }
        return patientService.getPatientDetails(token);
    }

    // 2. Create a New Patient
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        return patientService.createPatient(patient);
    }

    // 3. Patient Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginPatient(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    // 4. Get Patient Appointments
    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(@PathVariable Long id,
                                                                      @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode())
                    .body(Map.of("error", "Unauthorized access"));
        }
        return patientService.getPatientAppointment(id);
    }

    // 5. Filter Patient Appointments
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterAppointments(@PathVariable String condition,
                                                                   @PathVariable String name,
                                                                   @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode())
                    .body(Map.of("error", "Unauthorized access"));
        }
        return ResponseEntity.ok(service.filterPatient(condition, name, token));
    }
}
