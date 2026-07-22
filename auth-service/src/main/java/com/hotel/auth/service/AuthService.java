package com.hotel.auth.service;

import com.hotel.auth.dto.LoginRequest;
import com.hotel.auth.dto.LoginResponse;
import com.hotel.auth.dto.RegisterRequest;
import com.hotel.auth.entity.Role;
import com.hotel.auth.entity.User;
import com.hotel.auth.exception.DuplicateResourceException;
import com.hotel.auth.exception.ResourceNotFoundException;
import com.hotel.auth.repository.RoleRepository;
import com.hotel.auth.repository.UserRepository;
import com.hotel.common.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private static final String DEFAULT_ROLE = "CUSTOMER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse register(RegisterRequest request) {
        String loginUsername = request.resolveLoginUsername();
        if (loginUsername == null) {
            throw new IllegalArgumentException("Email or username is required");
        }
        if (userRepository.existsByUsername(loginUsername)) {
            throw new DuplicateResourceException("Username already taken: " + loginUsername);
        }
        String identityCard = request.getIdentityCard() == null ? null : request.getIdentityCard().trim();
        if (identityCard != null && !identityCard.isEmpty() && userRepository.existsByIdentityCard(identityCard)) {
            throw new DuplicateResourceException("Identity card already registered: " + identityCard);
        }
        String roleName = request.getRoleName() != null ? request.getRoleName() : DEFAULT_ROLE;
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        User user = new User();
        user.setUsername(loginUsername);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(role);
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIdentityCard(identityCard == null || identityCard.isEmpty() ? null : identityCard);
        user.setDepartment(request.getDepartment());
        user.setEmploymentStatus(request.getEmploymentStatus() != null ? request.getEmploymentStatus() : "AVAILABLE");
        user.setActive(request.getActive() == null || request.getActive());
        userRepository.save(user);

        return buildLoginResponse(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!user.isActive()) {
            throw new BadCredentialsException("Account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return buildLoginResponse(user);
    }

    /**
     * Sliding-session refresh: the caller's current token must still be valid (not expired); a
     * brand new token with a fresh expiry is issued for the same user. There's no separate
     * refresh-token entity/table here, which keeps things simple for this project's scope.
     */
    public LoginResponse refresh(String token) {
        String username;
        try {
            username = jwtUtil.parseToken(token).getSubject();
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid or expired token");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired token"));
        if (!user.isActive()) {
            throw new BadCredentialsException("Account is disabled");
        }
        return buildLoginResponse(user);
    }

    private LoginResponse buildLoginResponse(User user) {
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(),
                List.of(user.getRole().getRoleName()));
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getFullName(),
                user.getRole().getRoleName());
    }
}
