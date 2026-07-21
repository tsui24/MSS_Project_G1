package com.hotel.auth.service;

import com.hotel.auth.dto.ChangePasswordRequest;
import com.hotel.auth.dto.UpdateUserRequest;
import com.hotel.auth.dto.UserResponse;
import com.hotel.auth.entity.Role;
import com.hotel.auth.entity.User;
import com.hotel.auth.exception.ResourceNotFoundException;
import com.hotel.auth.repository.RoleRepository;
import com.hotel.auth.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserResponse> search(String roleName, Boolean active, Pageable pageable) {
        Page<User> page;
        if (roleName != null && active != null) {
            page = userRepository.findByRole_RoleNameAndActive(roleName, active, pageable);
        } else if (roleName != null) {
            page = userRepository.findByRole_RoleName(roleName, pageable);
        } else if (active != null) {
            page = userRepository.findByActive(active, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }
        return page.map(UserResponse::new);
    }

    public UserResponse getById(Long id) {
        return new UserResponse(findEntity(id));
    }

    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findEntity(id);
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getRoleName() != null) {
            Role role = roleRepository.findByRoleName(request.getRoleName())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRoleName()));
            user.setRole(role);
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getIdentityCard() != null) {
            String identityCard = request.getIdentityCard().trim();
            user.setIdentityCard(identityCard.isEmpty() ? null : identityCard);
        }
        if (request.getDepartment() != null) user.setDepartment(request.getDepartment());
        if (request.getEmploymentStatus() != null) user.setEmploymentStatus(request.getEmploymentStatus());
        return new UserResponse(userRepository.save(user));
    }

    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = findEntity(id);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Old password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void delete(Long id) {
        User user = findEntity(id);
        userRepository.delete(user);
    }

    User findEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
