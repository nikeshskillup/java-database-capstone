package com.project.back_end.services;


import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repository.AdminRepository;
import com.project.back_end.repository.DoctorRepository;
import com.project.back_end.repository.PatientRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // 1. Generate JWT token for a given email
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000); // 7 days

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Extract email (subject) from token
    public String extractEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    // 3. Validate token for a specific user type
    public boolean validateToken(String token, String user) {
        try {
            String email = extractEmail(token);
            if (email == null) return false;

            switch (user.toLowerCase()) {
                case "admin":
                    Optional<Admin> admin = adminRepository.findByUsername(email);
                    return admin.isPresent();
                case "doctor":
                    Optional<Doctor> doctor = doctorRepository.findByEmail(email);
                    return doctor.isPresent();
                case "patient":
                    Optional<Patient> patient = patientRepository.findByEmail(email);
                    return patient.isPresent();
                default:
                    return false;
            }

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 4. Get signing key used to sign and verify tokens
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
