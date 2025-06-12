package com.example.tsb.repositories;

import com.example.tsb.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    List<Appointment> findAll();
    Appointment findById(long id);
}
