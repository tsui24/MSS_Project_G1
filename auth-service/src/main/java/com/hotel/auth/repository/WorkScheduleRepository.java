package com.hotel.auth.repository;

import com.hotel.auth.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    List<WorkSchedule> findByWorkDateBetweenOrderByWorkDateAsc(LocalDate startDate, LocalDate endDate);
    boolean existsByStaffIdAndShiftIdAndWorkDate(Long staffId, Long shiftId, LocalDate workDate);
}
