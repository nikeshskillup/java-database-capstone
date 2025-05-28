package com.smartclinic.repository;

import com.smartclinic.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Find a patient by email
    Patient findByEmail(String email);

    // Find a patient by email or phone number
    Patient findByEmailOrPhone(String email, String phone);
}
