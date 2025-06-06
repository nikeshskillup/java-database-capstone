package com.project.back_end.repository;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH d.availability " +
           "WHERE a.doctor.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(@Param("doctorId") Long doctorId,
                                                             @Param("start") LocalDateTime start,
                                                             @Param("end") LocalDateTime end);

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.patient p LEFT JOIN FETCH a.doctor d " +
           "WHERE a.doctor.id = :doctorId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId,
            @Param("patientName") String patientName,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    @Query("SELECT a FROM Appointment a LEFT JOIN a.doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId")
    List<Appointment> findByDoctorNameContainingIgnoreCaseAndPatientId(@Param("doctorName") String doctorName,
                                                                      @Param("patientId") Long patientId);

    @Query("SELECT a FROM Appointment a LEFT JOIN a.doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId AND a.status = :status")
    List<Appointment> findByDoctorNameContainingIgnoreCaseAndPatientIdAndStatus(@Param("doctorName") String doctorName,
                                                                               @Param("patientId") Long patientId,
                                                                               @Param("status") int status);
}
