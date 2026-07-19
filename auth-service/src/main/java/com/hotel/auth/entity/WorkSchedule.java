package com.hotel.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "work_schedules", uniqueConstraints = @UniqueConstraint(columnNames = {"staff_id", "shift_id", "work_date"}))
public class WorkSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "staff_id", nullable = false)
    private User staff;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;
    @Column(nullable = false, length = 20)
    private String status = "SCHEDULED";

    public Long getId() { return id; }
    public User getStaff() { return staff; }
    public void setStaff(User staff) { this.staff = staff; }
    public Shift getShift() { return shift; }
    public void setShift(Shift shift) { this.shift = shift; }
    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
