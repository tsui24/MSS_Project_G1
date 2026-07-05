package com.hotel.auth.dto;

import com.hotel.auth.entity.Role;

public class RoleResponse {

    private Long id;
    private String roleName;
    private String description;

    public RoleResponse(Role role) {
        this.id = role.getId();
        this.roleName = role.getRoleName();
        this.description = role.getDescription();
    }

    public Long getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }
}
