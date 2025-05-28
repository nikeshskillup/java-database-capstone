package com.smartclinic.service;

import com.smartclinic.dto.AppointmentDTO;
import com.smartclinic.model.Appointment;
import com.smartclinic.model.Patient;
import com.smartclinic.repository.AppointmentRepository;
import com.smartclinic.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 1. Create Patient
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;  // success
        } catch (Exception e) {
            return 0;  // failure
        }
    }

    // 2. Get Patient Appointments
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        String emailFromToken = tokenService.extractEmail(token);
        Optional<Patient> optionalPatient = patientRepository.findByEmail(emailFromToken);
        
        if (optionalPatient.isEmpty() || !optionalPatient.get().getId().equals(id)) {
            response.put("message", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(id);
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(AppointmentDTO::fromAppointment)  // assuming a static conversion method
                .collect(Collectors.toList());

        response.put("appointments", appointmentDTOs);
        return ResponseEntity.ok(response);
    }

    // 3. Filter Appointments by Condition (past or future)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        List<Appointment> allAppointments = appointmentRepository.findByPatientId(id);
        LocalDateTime now = LocalDateTime.now();

        List<Appointment> filteredAppointments;

        if ("past".equalsIgnoreCase(condition)) {
            filteredAppointments = allAppointments.stream()
                    .filter(a -> a.getAppointmentTime().isBefore(now))
                    .collect(Collectors.toList());
        } else if ("future".equalsIgnoreCase(condition)) {
            filteredAppointments = allAppointments.stream()
                    .filter(a -> a.getAppointmentTime().isAfter(now))
                    .collect(Collectors.toList());
        } else {
            response.put("message", "Invalid condition parameter");
            return ResponseEntity.badRequest().body(response);
        }

        List<AppointmentDTO> dtos = filteredAppointments.stream()
                .map(AppointmentDTO::fromAppointment)
                .collect(Collectors.toList());

        response.put("appointments", dtos);
        return ResponseEntity.ok(response);
    }

    // 4. Filter by Doctor Name
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCase(patientId, name);
        
        if (appointments.isEmpty()) {
            response.put("message", "No appointments found for given doctor name");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        List<AppointmentDTO> dtos = appointments.stream()
                .map(AppointmentDTO::fromAppointment)
                .collect(Collectors.toList());

        response.put("appointments", dtos);
        return ResponseEntity.ok(response);
    }

    // 5. Filter by Doctor and Condition
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCase(patientId, name);
        LocalDateTime now = LocalDateTime.now();

        if (appointments.isEmpty()) {
            response.put("message", "No appointments found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        List<Appointment> filtered;
        if ("past".equalsIgnoreCase(condition)) {
            filtered = appointments.stream()
                    .filter(a -> a.getAppointmentTime().isBefore(now))
                    .collect(Collectors.toList());
        } else if ("future".equalsIgnoreCase(condition)) {
            filtered = appointments.stream()
                    .filter(a -> a.getAppointmentTime().isAfter(now))
                    .collect(Collectors.toList());
        } else {
            response.put("message", "Invalid condition parameter");
            return ResponseEntity.badRequest().body(response);
        }

        List<AppointmentDTO> dtos = filtered.stream()
                .map(AppointmentDTO::fromAppointment)
                .collect(Collectors.toList());

        response.put("appointments", dtos);
        return ResponseEntity.ok(response);
    }

    // 6. Get Patient Details by Token
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        String email = tokenService.extractEmail(token);
        Optional<Patient> patientOpt = patientRepository.findByEmail(email);

        if (patientOpt.isEmpty()) {
            response.put("message", "Patient not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("patient", patientOpt.get());
        return ResponseEntity.ok(response);
    }
}
