package com.project.back_end.services;

import com.project.back_end.models.Doctor;
// import com.project.back_end.models.Login;
import com.project.back_end.repository.AppointmentRepository;
import com.project.back_end.repository.DoctorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        // Define all possible time slots (example: 9AM to 5PM hourly)
        List<String> allSlots = Arrays.asList("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        var bookedAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        // Collect booked slots as strings (HH:mm)
        Set<String> bookedSlots = bookedAppointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString().substring(0,5))
                .collect(Collectors.toSet());

        // Filter out booked slots
        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
            return -1; // Already exists
        }
        try {
            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Error
        }
    }

    public int updateDoctor(Doctor doctor) {
        Optional<Doctor> existingOpt = doctorRepository.findById(doctor.getId());
        if (existingOpt.isPresent()) {
            return -1; // Not found
        }
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(Long id) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(id);
        if (doctorOpt.isPresent()) {
            return -1; // Not found
        }
        try {
            appointmentRepository.deleteAllByDoctorId(id); // delete appointments first
            doctorRepository.deleteById(id);
            return 1; // Success
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor() {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getEmail());
        if (doctor == null) {
            response.put("message", "Doctor not found");
            return ResponseEntity.badRequest().body(response);
        }
        if (!doctor.getPassword().equals(login.getPassword())) { // Consider hashing in real apps
            response.put("message", "Invalid password");
            return ResponseEntity.status(401).body(response);
        }
        // String token = tokenService.generateTokenForDoctor(doctor);
        String token = tokenService.generateToken(doctor.getEmail());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        response.put("doctors", doctors);
        return response;
    }

    public Map<String, Object> filterDoctorsByNameSpecialtyAndTime(String name, String Specialty, String amOrPm) {
        List<Doctor> filtered = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        filtered = filterDoctorByTime(filtered, amOrPm);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filtered);
        return response;
    }

    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> filtered = doctorRepository.findByNameLike(name);
        filtered = filterDoctorByTime(filtered, amOrPm);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filtered);
        return response;
    }

    public Map<String, Object> filterDoctorByNameAndSpecialty(String name, String specialty) {
        List<Doctor> filtered = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filtered);
        return response;
    }

    public Map<String, Object> filterDoctorByTimeAndSpecialty(String specialty, String amOrPm) {
        List<Doctor> filtered = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        filtered = filterDoctorByTime(filtered, amOrPm);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filtered);
        return response;
    }

    public Map<String, Object> filterDoctorBySpecialty(String specialty) {
        List<Doctor> filtered = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filtered);
        return response;
    }

    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> allDoctors = doctorRepository.findAll();
        List<Doctor> filtered = filterDoctorByTime(allDoctors, amOrPm);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filtered);
        return response;
    }

    // Private helper method
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        // Example logic:
        // AM = slots before 12:00, PM = slots from 12:00 onward
        // Check each doctor’s availability slots and keep those matching AM or PM

        if (amOrPm == null) return doctors;

        boolean filterAM = amOrPm.equalsIgnoreCase("AM");
        return doctors.stream()
                .filter(doctor -> {
                    List<String> availability = getDoctorAvailability(doctor.getId(), LocalDate.now()); // or some date
                    return availability.stream().anyMatch(slot -> {
                        int hour = Integer.parseInt(slot.split(":")[0]);
                        return filterAM ? hour < 12 : hour >= 12;
                    });
                })
                .collect(Collectors.toList());
    }
}
