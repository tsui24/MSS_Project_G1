package com.hotel.room.service;
import com.hotel.room.dto.*;
import com.hotel.room.entity.*;
import com.hotel.room.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DamageReportService {
    private final DamageReportRepository reports; private final RoomRepository rooms;
    public DamageReportService(DamageReportRepository reports, RoomRepository rooms){this.reports=reports;this.rooms=rooms;}
    public List<DamageReportResponse> find(Long staffId, Long reservationId){
        List<DamageReport> result = reservationId != null ? reports.findByReservationIdOrderByCreatedAtDesc(reservationId)
                : (staffId == null ? reports.findAll() : reports.findByStaffIdOrderByCreatedAtDesc(staffId));
        return result.stream().map(DamageReportResponse::new).toList();
    }
    public DamageReportResponse create(DamageReportRequest request){
        Room room=rooms.findById(request.getRoomId()).orElseThrow(() -> new IllegalArgumentException("Room not found: "+request.getRoomId()));
        DamageReport report=new DamageReport(); report.setStaffId(request.getStaffId()); report.setRoom(room);
        report.setReservationId(request.getReservationId()); report.setItemName(request.getItemName());
        report.setDescription(request.getDescription()); report.setQuantity(request.getQuantity()); report.setPenaltyAmount(request.getPenaltyAmount());
        return new DamageReportResponse(reports.save(report));
    }
}
