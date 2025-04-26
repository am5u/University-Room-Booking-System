package com.University.RoomBooking.Service;

import com.University.RoomBooking.Dto.RoomSearchRequest;
import com.University.RoomBooking.Model.Room;
import com.University.RoomBooking.Model.Booking;
import com.University.RoomBooking.Repository.RoomRepository;
import com.University.RoomBooking.Repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public List<Room> findAvailableRooms(RoomSearchRequest request) {
        System.out.println("Received search request: " + request);
        
        // Parse date and time
        LocalDate date = LocalDate.parse(request.getDate());
        LocalTime time = LocalTime.parse(request.getTime());
        
        // Create start and end time
        LocalDateTime startTime = LocalDateTime.of(date, time);
        LocalDateTime endTime = startTime.plusHours(request.getDuration());

        System.out.println("Searching for rooms between " + startTime + " and " + endTime);

        // Find available rooms
        List<Room> availableRooms = roomRepository.findAvailableRooms(startTime, endTime);
        System.out.println("Total available rooms before filtering: " + availableRooms.size());
        System.out.println("Available rooms: " + availableRooms);

        // Filter by capacity if specified
        if (request.getCapacity() != null) {
            availableRooms = availableRooms.stream()
                .filter(room -> room.getCapacity() >= request.getCapacity())
                .toList();
            System.out.println("Rooms after capacity filter: " + availableRooms.size());
        }

        // Filter by room type if specified
        if (request.getRoomType() != null && !request.getRoomType().trim().isEmpty()) {
            String searchType = request.getRoomType().trim();
            System.out.println("Searching for room type: " + searchType);
            availableRooms = availableRooms.stream()
                .filter(room -> {
                    boolean matches = room.getRoomType().equalsIgnoreCase(searchType);
                    System.out.println("Room " + room.getId() + " type: " + room.getRoomType() + " matches: " + matches);
                    return matches;
                })
                .toList();
            System.out.println("Rooms after type filter: " + availableRooms.size());
        }

        return availableRooms;
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public boolean checkRoomAvailability(Long roomId, String date, String time, int duration) {
        // Parse date and time
        LocalDate bookingDate = LocalDate.parse(date);
        LocalTime bookingTime = LocalTime.parse(time);
        
        // Create start and end time
        LocalDateTime startTime = LocalDateTime.of(bookingDate, bookingTime);
        LocalDateTime endTime = startTime.plusHours(duration);

        // Get room
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Check for conflicting bookings
        List<Booking> conflictingBookings = bookingRepository.findByRoomId(roomId).stream()
            .filter(booking -> booking.getStatus().equals("CONFIRMED")|| booking.getStatus().equals("PENDING"))
            .filter(booking -> 
                (booking.getStartTime().isBefore(endTime) && booking.getEndTime().isAfter(startTime)) ||
                (booking.getStartTime().isAfter(startTime) && booking.getStartTime().isBefore(endTime)) ||
                (booking.getEndTime().isAfter(startTime) && booking.getEndTime().isBefore(endTime))
            )
            .toList();

        return conflictingBookings.isEmpty();
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room roomDetails) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        room.setName(roomDetails.getName());
        room.setCapacity(roomDetails.getCapacity());
        room.setLocation(roomDetails.getLocation());
        room.setDescription(roomDetails.getDescription());
        room.setAvailableEquipment(roomDetails.getAvailableEquipment());
        room.setImageUrl(roomDetails.getImageUrl());
        room.setRoomType(roomDetails.getRoomType());
        
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        roomRepository.delete(room);
    }
} 