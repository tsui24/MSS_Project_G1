package com.hotel.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Customer FE sends {@code email}; admin staff creation still sends {@code username}.
 * Either is accepted and stored as {@code users.username} (login identity).
 */
public class RegisterRequest {

    @Size(min = 4, max = 100)
    private String username;

    @Email(message = "Invalid email")
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank
    @Size(min = 2, max = 150)
    private String fullName;

    /**
     * Optional role name (ADMIN, RECEPTIONIST, HOUSEKEEPING, CUSTOMER). Defaults to CUSTOMER
     * when omitted, since this endpoint is public and self-service registration should not
     * let callers grant themselves staff privileges without one existing already.
     */
    private String roleName;

    @Pattern(regexp = "^$|^\\d{10,11}$", message = "Phone number must be 10-11 digits")
    private String phoneNumber;

    @Pattern(regexp = "^$|^\\d{9,12}$", message = "Identity card must be 9-12 digits")
    private String identityCard;

    private String department;
    private String employmentStatus;
    private Boolean active;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    /** Login identity: prefer email (customer FE), else username (admin/staff). */
    public String resolveLoginUsername() {
        if (email != null && !email.isBlank()) {
            return email.trim().toLowerCase();
        }
        if (username != null && !username.isBlank()) {
            return username.trim();
        }
        return null;
    }
}
