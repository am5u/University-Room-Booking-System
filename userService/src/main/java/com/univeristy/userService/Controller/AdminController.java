package com.univeristy.userService.Controller;
import com.univeristy.userService.Model.User;
import com.univeristy.userService.Model.Role;
import com.univeristy.userService.Service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.univeristy.userService.Annotation.AdminOnly;


import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    
    private final UserService userService;

    @AdminOnly
    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard() {
        return ResponseEntity.ok("Welcome to the Admin Dashboard");
    }

    

    

    @GetMapping("/users")
    @AdminOnly
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/roles")
    @AdminOnly
    public ResponseEntity<Role[]> getAllRoles() {
        return ResponseEntity.ok(Role.values());
    }

    @PostMapping("/users/{userId}/role")
    @AdminOnly
   
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long userId,
            @RequestBody RoleUpdateRequest request
    ) {
        User updatedUser = userService.updateUserRole(userId, request.getRole());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{userId}")
    @AdminOnly
   
    public ResponseEntity<String> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            String adminUserId = request.getHeader("X-User-ID");
            if (adminUserId != null) {
                request.setAttribute("userId", adminUserId);
            }
            
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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

