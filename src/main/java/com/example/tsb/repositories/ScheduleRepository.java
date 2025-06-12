package com.example.tsb.repositories;

import com.example.tsb.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    Schedule findByDate(LocalDateTime localDateTime);
    Schedule findById(long id);

}
