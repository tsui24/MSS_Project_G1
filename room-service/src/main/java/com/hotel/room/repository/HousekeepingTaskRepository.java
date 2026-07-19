package com.hotel.room.repository;

import com.hotel.room.entity.HousekeepingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTask, Long> {
    List<HousekeepingTask> findAllByOrderByCreatedAtDesc();
    List<HousekeepingTask> findByStaffIdOrderByCreatedAtDesc(Long staffId);
    boolean existsByRoomIdAndStatusIn(Long roomId, List<String> statuses);
    List<HousekeepingTask> findByStatusIn(List<String> statuses);
}
