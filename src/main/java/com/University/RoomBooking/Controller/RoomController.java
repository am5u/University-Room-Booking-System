package com.University.RoomBooking.Controller;

import com.University.RoomBooking.Dto.RoomSearchRequest;
import com.University.RoomBooking.Model.Room;
import com.University.RoomBooking.Service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(@RequestBody RoomSearchRequest request) {
        List<Room> availableRooms = roomService.findAvailableRooms(request);
        return ResponseEntity.ok(availableRooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/{roomId}/availability")
    public ResponseEntity<?> checkRoomAvailability(
            @PathVariable Long roomId,
            @RequestBody(required = false) Map<String, Object> request) {
        try {
            // Validate request body
            if (request == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Request body is required"));
            }

            // Get and validate date
            Object dateObj = request.get("date");
            if (dateObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Date is required"));
            }
            String date = dateObj.toString();
            if (date.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Date cannot be empty"));
            }

            // Get and validate time
            Object timeObj = request.get("time");
            if (timeObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Time is required"));
            }
            String time = timeObj.toString();
            if (time.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Time cannot be empty"));
            }

            // Get and validate duration
            Object durationObj = request.get("duration");
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

            boolean isAvailable = roomService.checkRoomAvailability(roomId, date, time, duration);
            return ResponseEntity.ok(Map.of("available", isAvailable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
} 