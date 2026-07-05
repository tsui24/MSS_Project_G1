package com.hotel.auth.service;

import com.hotel.auth.dto.RoleRequest;
import com.hotel.auth.dto.RoleResponse;
import com.hotel.auth.entity.Role;
import com.hotel.auth.exception.DuplicateResourceException;
import com.hotel.auth.exception.ResourceNotFoundException;
import com.hotel.auth.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(RoleResponse::new).toList();
    }

    public RoleResponse getById(Long id) {
        return new RoleResponse(findEntity(id));
    }

    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByRoleName(request.getRoleName())) {
            throw new DuplicateResourceException("Role already exists: " + request.getRoleName());
        }
        Role role = new Role(request.getRoleName(), request.getDescription());
        return new RoleResponse(roleRepository.save(role));
    }

    public RoleResponse update(Long id, RoleRequest request) {
        Role role = findEntity(id);
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        return new RoleResponse(roleRepository.save(role));
    }

    public void delete(Long id) {
        Role role = findEntity(id);
        roleRepository.delete(role);
    }

    private Role findEntity(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }
}
