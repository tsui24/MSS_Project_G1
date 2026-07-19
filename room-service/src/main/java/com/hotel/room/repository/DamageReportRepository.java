package com.hotel.room.repository;
import com.hotel.room.entity.DamageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DamageReportRepository extends JpaRepository<DamageReport, Long> {
    List<DamageReport> findByStaffIdOrderByCreatedAtDesc(Long staffId);
    List<DamageReport> findByReservationIdOrderByCreatedAtDesc(Long reservationId);
}
