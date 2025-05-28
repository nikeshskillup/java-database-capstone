package com.smartclinic.service;

import com.smartclinic.model.*;
import com.smartclinic.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // 1. Validate Token
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        boolean isValid = tokenService.validateToken(token, user);
        if (!isValid) {
            response.put("message", "Token is invalid or expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        response.put("message", "Token is valid");
        return ResponseEntity.ok(response);
    }

    // 2. Validate Admin Login
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        Optional<Admin> adminOpt = adminRepository.findByUsername(receivedAdmin.getUsername());

        if (adminOpt.isEmpty() || !adminOpt.get().getPassword().equals(receivedAdmin.getPassword())) {
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = tokenService.generateToken(adminOpt.get().getUsername());
        response.put("token", token);
        response.put("message", "Admin authenticated successfully");
        return ResponseEntity.ok(response);
    }

    // 3. Filter Doctor by name, specialty and time
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        if ((name == null || name.isEmpty()) && (specialty == null || specialty.isEmpty()) && (time == null || time.isEmpty())) {
            // Return all doctors if no filter provided
            return Map.of("doctors", doctorService.getDoctors());
        }

        if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty() && time != null && !time.isEmpty()) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } else if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty()) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if (name != null && !name.isEmpty() && time != null && !time.isEmpty()) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        } else if (specialty != null && !specialty.isEmpty() && time != null && !time.isEmpty()) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        } else if (name != null && !name.isEmpty()) {
            return doctorService.findDoctorByName(name);
        } else if (specialty != null && !specialty.isEmpty()) {
            return doctorService.filterDoctorBySpecility(specialty);
        } else if (time != null && !time.isEmpty()) {
            return doctorService.filterDoctorsByTime(time);
        } else {
            return Map.of("doctors", doctorService.getDoctors());
        }
    }

    // 4. Validate Appointment availability
    public int validateAppointment(Appointment appointment) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctorId());
        if (doctorOpt.isEmpty()) {
            return -1;  // Doctor doesn't exist
        }

        // Get available slots for that doctor on the appointment date
        var availableSlots = doctorService.getDoctorAvailability(appointment.getDoctorId(), appointment.getAppointmentTime().toLocalDate());

        String appointmentTimeStr = appointment.getAppointmentTime().toLocalTime().toString();
        if (availableSlots.contains(appointmentTimeStr)) {
            return 1;  // Valid appointment time
        }
        return 0;  // Time unavailable
    }

    // 5. Validate patient existence by email or phone
    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()).isEmpty();
    }

    // 6. Validate patient login
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        Optional<Patient> patientOpt = patientRepository.findByEmail(login.getEmail());
        if (patientOpt.isEmpty() || !patientOpt.get().getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = tokenService.generateToken(patientOpt.get().getEmail());
        response.put("token", token);
        response.put("message", "Patient authenticated successfully");
        return ResponseEntity.ok(response);
    }

    // 7. Filter patient appointments by condition and/or doctor name
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();

        // Extract email from token and find patient
        String email = tokenService.extractEmail(token);
        Optional<Patient> patientOpt = patientRepository.findByEmail(email);
        if (patientOpt.isEmpty()) {
            response.put("message", "Patient not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Long patientId = patientOpt.get().getId();

        try {
            if ((condition == null || condition.isEmpty()) && (name == null || name.isEmpty())) {
                // No filters, get all appointments for patient
                return patientService.getPatientAppointment(patientId, token);
            } else if (condition != null && !condition.isEmpty() && (name == null || name.isEmpty())) {
                // Filter by condition only
                return patientService.filterByCondition(condition, patientId);
            } else if ((condition == null || condition.isEmpty()) && name != null && !name.isEmpty()) {
                // Filter by doctor name only
                return patientService.filterByDoctor(name, patientId);
            } else {
                // Filter by both condition and doctor name
                return patientService.filterByDoctorAndCondition(condition, name, patientId);
            }
        } catch (Exception e) {
            response.put("message", "Error filtering patient appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
