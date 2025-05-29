package com.smartclinic.controller;

import com.smartclinic.model.Doctor;
import com.smartclinic.model.Login;
import com.smartclinic.service.DoctorService;
import com.smartclinic.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    @Autowired
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    // 1. Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(@PathVariable String user,
                                                                     @PathVariable Long doctorId,
                                                                     @PathVariable String date,
                                                                     @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, user);
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", "Unauthorized access"));
        }
        return doctorService.getDoctorAvailability(doctorId, date);
    }

    // 2. Get List of Doctors
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDoctors() {
        return doctorService.getDoctors();
    }

    // 3. Add New Doctor (Admin only)
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addDoctor(@PathVariable String token,
                                                         @RequestBody Doctor doctor) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", "Unauthorized access"));
        }
        return doctorService.saveDoctor(doctor);
    }

    // 4. Doctor Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    // 5. Update Doctor Details
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(@PathVariable String token,
                                                            @RequestBody Doctor doctor) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", "Unauthorized access"));
        }
        return doctorService.updateDoctor(doctor);
    }

    // 6. Delete Doctor (Admin only)
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable Long id,
                                                            @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", "Unauthorized access"));
        }
        return doctorService.deleteDoctor(id);
    }

    // 7. Filter Doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(@PathVariable String name,
                                                             @PathVariable String time,
                                                             @PathVariable String speciality) {
        return ResponseEntity.ok(service.filterDoctor(name, speciality, time));
    }
}
