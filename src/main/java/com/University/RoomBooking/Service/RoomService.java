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
} 