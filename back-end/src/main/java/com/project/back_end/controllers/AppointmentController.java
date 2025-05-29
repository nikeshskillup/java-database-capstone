package com.smartclinic.controller;

import com.smartclinic.model.Appointment;
import com.smartclinic.service.AppointmentService;
import com.smartclinic.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // GET: Retrieve appointments for a specific date and patient name (Doctor access only)
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(@PathVariable String date,
                                                               @PathVariable String patientName,
                                                               @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", "Unauthorized access"));
        }

        return appointmentService.getAppointment(date, patientName);
    }

    // POST: Book a new appointment (Patient access only)
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@PathVariable String token,
                                                               @RequestBody Appointment appointment) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", "Unauthorized access"));
        }

        int validationStatus = service.validateAppointment(appointment);
        if (validationStatus == -1) {
            return ResponseEntity.status(404).body(Map.of("error", "Doctor not found"));
        } else if (validationStatus == 0) {
            return ResponseEntity.status(409).body(Map.of("error", "Time slot not available"));
        }

        return appointmentService.bookAppointment(appointment);
    }

    // PUT: Update an existing appointment (Patient access only)
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@PathVariable String token,
                                                                 @RequestBody Appointment appointment) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", "Unauthorized access"));
        }

        return appointmentService.updateAppointment(appointment);
    }

    // DELETE: Cancel an appointment by ID (Patient access only)
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable Long id,
                                                                 @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("error", "Unauthorized access"));
        }

        return appointmentService.cancelAppointment(id);
    }
}
