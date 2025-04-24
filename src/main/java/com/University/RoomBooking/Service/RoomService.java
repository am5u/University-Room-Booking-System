package com.University.RoomBooking.Service;

import com.University.RoomBooking.Dto.RoomSearchRequest;
import com.University.RoomBooking.Model.Room;
import com.University.RoomBooking.Repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public List<Room> findAvailableRooms(RoomSearchRequest request) {
        // Parse date and time
        LocalDate date = LocalDate.parse(request.getDate());
        LocalTime time = LocalTime.parse(request.getTime());
        
        // Create start and end time
        LocalDateTime startTime = LocalDateTime.of(date, time);
        LocalDateTime endTime = startTime.plusHours(request.getDuration());

        // Find available rooms
        List<Room> availableRooms = roomRepository.findAvailableRooms(startTime, endTime);

        // Filter by capacity if specified
        if (request.getCapacity() != null) {
            availableRooms = availableRooms.stream()
                .filter(room -> room.getCapacity() >= request.getCapacity())
                .toList();
        }

        return availableRooms;
    }
} 