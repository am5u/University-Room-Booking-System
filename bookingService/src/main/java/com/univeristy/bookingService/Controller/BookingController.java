package com.univeristy.bookingService.Controller;

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
    public ResponseEntity<?> createBooking(@RequestBody(required = false) Map<String, Object> bookingRequest) {
        try {
            // Validate request body
            if (bookingRequest == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Request body is required"));
            }

            // Get and validate roomId
            Object roomIdObj = bookingRequest.get("roomId");
            if (roomIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Room ID is required"));
            }
            Long roomId;
            try {
                roomId = Long.parseLong(roomIdObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid room ID format"));
            }

            // Get and validate userId
            Object userIdObj = bookingRequest.get("userId");
            if (userIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "User ID is required"));
            }
            Long userId;
            try {
                userId = Long.parseLong(userIdObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid user ID format"));
            }

            // Get and validate date
            Object dateObj = bookingRequest.get("date");
            if (dateObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Date is required"));
            }
            String date = dateObj.toString();
            if (date.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Date cannot be empty"));
            }

            // Get and validate time
            Object timeObj = bookingRequest.get("time");
            if (timeObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Time is required"));
            }
            String time = timeObj.toString();
            if (time.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Time cannot be empty"));
            }

            // Get and validate duration
            Object durationObj = bookingRequest.get("duration");
            if (durationObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Duration is required"));
            }
            int duration;
            try {
                duration = Integer.parseInt(durationObj.toString());
                if (duration <= 0 || duration > 8) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Duration must be between 1 and 8 hours"));
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid duration format"));
            }

            // Get and validate purpose
            Object purposeObj = bookingRequest.get("purpose");
            if (purposeObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Purpose is required"));
            }
            String purpose = purposeObj.toString();
            if (purpose.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Purpose cannot be empty"));
            }

            Booking booking = bookingService.createBooking(roomId, userId, date, time, duration, purpose);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<?> getUserBookingHistory(@PathVariable Long userId) {
        try {
            List<Booking> bookings = bookingService.getUserBookings(userId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            Booking booking = bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<String> acceptBooking(@PathVariable Long id) {
        try {
            bookingService.acceptBooking(id);
            return ResponseEntity.ok("Booking with ID " + id + " has been accepted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectBooking(@PathVariable Long id) {
        try {
            String result = bookingService.rejectBooking(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}