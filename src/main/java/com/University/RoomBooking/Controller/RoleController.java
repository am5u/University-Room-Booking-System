package com.University.RoomBooking.Controller;

import com.University.RoomBooking.Model.Role;
import com.University.RoomBooking.Model.User;
import com.University.RoomBooking.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Admin requested all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long userId,
            @RequestBody RoleUpdateRequest request
    ) {
        log.info("Admin updating role for user {} to {}", userId, request.getRole());
        User updatedUser = userService.updateUserRole(userId, request.getRole());
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role[]> getAllRoles() {
        log.info("Admin requested all roles");
        return ResponseEntity.ok(Role.values());
    }
}

class RoleUpdateRequest {
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
} 