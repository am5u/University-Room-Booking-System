package com.univeristy.bookingService.Controller;      

import com.univeristy.bookingService.Dto.RoomSearchRequest;
import com.univeristy.bookingService.Model.Room;
import com.univeristy.bookingService.Service.RoomService;
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
        return ResponseEntity.ok(roomService.findAvailableRooms(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{roomId}/availability")
    public ResponseEntity<?> checkRoomAvailability(
            @PathVariable Long roomId,
            @RequestBody RoomSearchRequest request) {
        try {
            validateAvailabilityRequest(request);
            boolean isAvailable = roomService.checkRoomAvailability(
                roomId, 
                request.getDate(), 
                request.getTime(), 
                request.getDuration()
            );
            return ResponseEntity.ok(Map.of("available", isAvailable));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private void validateAvailabilityRequest(RoomSearchRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
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
    }
} 