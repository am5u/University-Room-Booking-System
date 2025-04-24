package com.University.RoomBooking.Controller;

import com.University.RoomBooking.Dto.RoomSearchRequest;
import com.University.RoomBooking.Model.Room;
import com.University.RoomBooking.Service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(@RequestBody RoomSearchRequest request) {
        List<Room> availableRooms = roomService.findAvailableRooms(request);
        return ResponseEntity.ok(availableRooms);
    }
} 