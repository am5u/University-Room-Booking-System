package com.univeristy.bookingService.Controller;

import com.univeristy.bookingService.Annotation.AuditAction;
import com.univeristy.bookingService.Dto.BookingRequest;
import com.univeristy.bookingService.Model.Booking;
import com.univeristy.bookingService.Service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @AuditAction("Create Booking")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            validateBookingRequest(request);
            Booking booking = bookingService.createBooking(
                request.getRoomId(),
                request.getUserId(),
                request.getDate(),
                request.getTime(),
                request.getDuration(),
                request.getPurpose()
            );
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private void validateBookingRequest(BookingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getRoomId() == null) {
            throw new IllegalArgumentException("Room ID is required");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (request.getDate() == null || request.getDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Date is required");
        }
        if (request.getTime() == null || request.getTime().trim().isEmpty()) {
            throw new IllegalArgumentException("Time is required");
        }
        if (request.getDuration() == null || request.getDuration() <= 0 || request.getDuration() > 8) {
            throw new IllegalArgumentException("Duration must be between 1 and 8 hours");
        }
        if (request.getPurpose() == null || request.getPurpose().trim().isEmpty()) {
            throw new IllegalArgumentException("Purpose is required");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<Booking>> getUserBookingHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @PostMapping("/{bookingId}/cancel")
    @AuditAction("Cancel Booking")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptBooking(@PathVariable Long id) {
        try {
            bookingService.acceptBooking(id);
            return ResponseEntity.ok(Map.of("message", "Booking with ID " + id + " has been accepted"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(Map.of("message", bookingService.rejectBooking(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}