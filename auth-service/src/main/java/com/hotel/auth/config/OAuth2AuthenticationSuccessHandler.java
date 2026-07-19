package com.hotel.auth.config;

import com.hotel.auth.entity.Role;
import com.hotel.auth.entity.User;
import com.hotel.auth.exception.ResourceNotFoundException;
import com.hotel.auth.repository.RoleRepository;
import com.hotel.auth.repository.UserRepository;
import com.hotel.common.security.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String DEFAULT_ROLE = "CUSTOMER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final String frontendRedirectUri;

    public OAuth2AuthenticationSuccessHandler(UserRepository userRepository,
                                               RoleRepository roleRepository,
                                               PasswordEncoder passwordEncoder,
                                               JwtUtil jwtUtil,
                                               @Value("${app.oauth2.frontend-redirect-uri}") String frontendRedirectUri) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.frontendRedirectUri = frontendRedirectUri;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = normalizeEmail(oauth2User.getAttribute("email"));
        String name = oauth2User.getAttribute("name");

        // Read these Google attributes now so the flow remains ready for provider metadata
        // without changing the current database schema unnecessarily.
        String googleSubjectId = oauth2User.getAttribute("sub");
        String picture = oauth2User.getAttribute("picture");

        if (email == null) {
            redirectWithError(response, "Google account did not provide a valid email address");
            return;
        }

        User user = userRepository.findByUsername(email)
                .orElseGet(() -> createGoogleUser(email, name));

        if (!user.isActive()) {
            redirectWithError(response, "Account is disabled");
            return;
        }

        String roleName = user.getRole().getRoleName();
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), List.of(roleName));
        String redirectUri = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("token", token)
                .build()
                .encode()
                .toUriString();
        response.sendRedirect(redirectUri);
    }

    private User createGoogleUser(String email, String name) {
        Role role = roleRepository.findByRoleName(DEFAULT_ROLE)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + DEFAULT_ROLE));

        User user = new User();
        user.setUsername(email);
        user.setFullName(name == null || name.isBlank() ? email : name.trim());
        user.setRole(role);
        user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID() + ":" + UUID.randomUUID()));
        return userRepository.save(user);
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        return normalized.isBlank() || !normalized.contains("@") ? null : normalized;
    }

    private void redirectWithError(HttpServletResponse response, String message) throws IOException {
        String redirectUri = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("error", message)
                .build()
                .encode()
                .toUriString();
        response.sendRedirect(redirectUri);
    }
}
