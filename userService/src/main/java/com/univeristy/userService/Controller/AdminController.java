package com.univeristy.userService.Controller;
import com.univeristy.userService.Model.User;
import com.univeristy.userService.Model.Booking;
import com.univeristy.userService.Model.Role;
import com.univeristy.userService.Service.BookingService;
import com.univeristy.userService.Service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.univeristy.userService.Annotation.AdminOnly;
import com.univeristy.userService.Annotation.AuditAction;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final BookingService bookingService;
    private final UserService userService;

    @AdminOnly
    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard() {
        return ResponseEntity.ok("Welcome to the Admin Dashboard");
    }

    @AdminOnly
    @GetMapping("/allbookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        System.out.println("AdminController: Fetching all bookings");
        List<Booking> bookings = bookingService.getAllBookings();
        System.out.println("AdminController: Retrieved " + bookings.size() + " bookings");

        // Debug output for each booking
        for (Booking booking : bookings) {
            System.out.println("Booking ID: " + booking.getId() +
                               ", User ID: " + booking.getUserId() +
                               ", Room ID: " + booking.getRoomId() +
                               ", User Name: " + booking.getUserName() +
                               ", Room Name: " + booking.getRoomName());

            // Check if user object is properly created
            if (booking.getUser() != null) {
                System.out.println("  User object: name=" + booking.getUser().getName() +
                                   ", role=" + booking.getUser().getRole() +
                                   ", department=" + booking.getUser().getDepartment());
            } else {
                System.out.println("  User object is null");
            }

            // Check if room object is properly created
            if (booking.getRoom() != null) {
                System.out.println("  Room object: name=" + booking.getRoom().getName());
            } else {
                System.out.println("  Room object is null");
            }
        }

        return ResponseEntity.ok(bookings);
    }

    @AdminOnly
    @AuditAction("Accept Booking")
    @PostMapping("/acceptbooking")
    public ResponseEntity<String> acceptBooking(@RequestParam Long Id, HttpServletRequest request) {
        try {
            // Store the admin user ID in the request for audit purposes
            String adminUserId = request.getHeader("X-User-ID");
            System.out.println("AdminController - X-User-ID header: " + adminUserId);

            if (adminUserId != null) {
                request.setAttribute("userId", adminUserId);
                System.out.println("AdminController - Setting userId attribute: " + adminUserId);
            }

            bookingService.acceptBooking(Id);
            return ResponseEntity.ok("Reservation with ID " + Id + " has been accepted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @AdminOnly
    @PostMapping("/rejectbooking")
    @AuditAction("Reject Booking")
    public ResponseEntity<String> rejectBooking(@RequestParam Long Id, HttpServletRequest request) {
        try {
            // Store the admin user ID in the request for audit purposes
            String adminUserId = request.getHeader("X-User-ID");
            System.out.println("AdminController - X-User-ID header: " + adminUserId);

            if (adminUserId != null) {
                request.setAttribute("userId", adminUserId);
                System.out.println("AdminController - Setting userId attribute: " + adminUserId);
            }

            String result = bookingService.rejectBooking(Id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
    @AuditAction("Change User Role")
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long userId,
            @RequestBody RoleUpdateRequest request
    ) {
        User updatedUser = userService.updateUserRole(userId, request.getRole());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{userId}")
    @AdminOnly
    @AuditAction("Delete User")
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

