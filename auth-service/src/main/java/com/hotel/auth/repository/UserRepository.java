package com.hotel.auth.repository;

import com.hotel.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByIdentityCard(String identityCard);
    Optional<User> findByIdentityCard(String identityCard);
    Page<User> findByRole_RoleName(String roleName, Pageable pageable);
    Page<User> findByActive(boolean active, Pageable pageable);
    Page<User> findByRole_RoleNameAndActive(String roleName, boolean active, Pageable pageable);
}
