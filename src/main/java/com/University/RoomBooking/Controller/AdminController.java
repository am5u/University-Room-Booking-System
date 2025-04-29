package com.University.RoomBooking.Controller;
import com.University.RoomBooking.Model.User;
import com.University.RoomBooking.Model.Booking;
import com.University.RoomBooking.Model.Role;
import com.University.RoomBooking.Service.BookingService;
import com.University.RoomBooking.Service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.University.RoomBooking.Security.Annotation.AdminOnly;
import com.University.RoomBooking.Security.Annotation.AuditAction;

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
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @AdminOnly
    @AuditAction("Accept Booking")
    @PostMapping("/acceptbooking")
    public ResponseEntity<String> acceptBooking(@RequestParam Long Id) {
        try {
            bookingService.acceptBooking(Id);
            return ResponseEntity.ok("Reservation with ID " + Id + " has been accepted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @AdminOnly
    @PostMapping("/rejectbooking")
    @AuditAction("Reject Booking")
    public ResponseEntity<String> rejectBooking(@RequestParam Long Id) {
        try {
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
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long userId,
            @RequestBody RoleUpdateRequest request
    ) {
        User updatedUser = userService.updateUserRole(userId, request.getRole());
        return ResponseEntity.ok(updatedUser);
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

    