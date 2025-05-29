package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.repository.AppointmentRepository;
import com.project.back_end.repository.PatientRepository;
import com.project.back_end.repository.DoctorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            // Log error here
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());
        Map<String, String> response = new HashMap<>();

        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        // TODO: validate appointment using service.validateAppointment()

        appointmentRepository.save(appointment);
        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, String>> cancelAppointment(Long id, String token) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        Map<String, String> response = new HashMap<>();

        if (appointmentOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment appointment = appointmentOpt.get();

        // Validate token matches patient who booked the appointment
        Long patientIdFromToken = tokenService.extractPatientId(token);
        if (!appointment.getPatient().getId().equals(patientIdFromToken)) {
            response.put("message", "Unauthorized to cancel this appointment");
            return ResponseEntity.status(403).body(response);
        }

        appointmentRepository.delete(appointment);
        response.put("message", "Appointment cancelled successfully");
        return ResponseEntity.ok(response);
    }

    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();

        Long doctorId = tokenService.extractDoctorId(token);
        if (doctorId == null) {
            result.put("message", "Invalid doctor token");
            return result;
        }

        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(LocalTime.MAX);

        List<Appointment> appointments;
        if (pname == null || pname.isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, startDateTime, endDateTime);
        } else {
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(doctorId, pname, startDateTime, endDateTime);
        }

        result.put("appointments", appointments);
        return result;
    }

    // Additional helper method: validateAppointment() can be implemented here.
}
