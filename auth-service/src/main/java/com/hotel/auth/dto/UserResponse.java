package com.hotel.auth.dto;

import com.hotel.auth.entity.User;

public class UserResponse {

    private Long id;
    private String username;
    private String fullName;
    private String roleName;
    private boolean active;
    private String phoneNumber;
    private String department;
    private String employmentStatus;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.roleName = user.getRole() != null ? user.getRole().getRoleName() : null;
        this.active = user.isActive();
        this.phoneNumber = user.getPhoneNumber();
        this.department = user.getDepartment();
        this.employmentStatus = user.getEmploymentStatus();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRoleName() {
        return roleName;
    }

    public boolean isActive() {
        return active;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }
}
