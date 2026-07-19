package com.hotel.auth.controller;

import com.hotel.auth.dto.ChangePasswordRequest;
import com.hotel.auth.dto.UpdateUserRequest;
import com.hotel.auth.dto.UserResponse;
import com.hotel.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/users")
@Tag(name = "Users", description = "User management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "List users, paginated and optionally filtered by role name")
    public ResponseEntity<Page<UserResponse>> search(@RequestParam(name = "role", required = false) String role,
                                                      @RequestParam(name = "isActive", required = false) Boolean isActive,
                                                      @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.search(role, isActive, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by id")
    public ResponseEntity<UserResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user's full name and/or role")
    public ResponseEntity<UserResponse> update(@PathVariable("id") Long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Change a user's password (requires the correct old password)")
    public ResponseEntity<Void> changePassword(@PathVariable("id") Long id, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
