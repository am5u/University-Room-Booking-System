package com.univeristy.bookingService.Service;      

import com.univeristy.bookingService.Dto.RoomSearchRequest;
import com.univeristy.bookingService.Model.Room;
import com.univeristy.bookingService.Model.Booking;
import com.univeristy.bookingService.Repository.RoomRepository;
import com.univeristy.bookingService.Repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public List<Room> findAvailableRooms(RoomSearchRequest request) {
       
        LocalDate date = LocalDate.parse(request.getDate());
        LocalTime time = LocalTime.parse(request.getTime());
        
        LocalDateTime startTime = LocalDateTime.of(date, time);
        LocalDateTime endTime = startTime.plusHours(request.getDuration());

        List<Room> availableRooms = roomRepository.findAvailableRooms(startTime, endTime);
       
        if (request.getCapacity() != null) {
            availableRooms = availableRooms.stream()
                .filter(room -> room.getCapacity() >= request.getCapacity())
                .toList();
        
        }
        if (request.getRoomType() != null && !request.getRoomType().trim().isEmpty()) {
            String searchType = request.getRoomType().trim();
            availableRooms = availableRooms.stream()
                .filter(room -> {
                    boolean matches = room.getRoomType().equalsIgnoreCase(searchType);
                    return matches;
                })
                .toList();
        }

        return availableRooms;
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public boolean checkRoomAvailability(Long roomId, String date, String time, int duration) {
        LocalDate bookingDate = LocalDate.parse(date);
        LocalTime bookingTime = LocalTime.parse(time);
        LocalDateTime startTime = LocalDateTime.of(bookingDate, bookingTime);
        LocalDateTime endTime = startTime.plusHours(duration);
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

    // public List<Room> getAllRooms() {
    //     return roomRepository.findAll();
    // }

    // public Room createRoom(Room room) {
    //     return roomRepository.save(room);
    // }

    // public Room updateRoom(Long id, Room roomDetails) {
    //     Room room = roomRepository.findById(id)
    //             .orElseThrow(() -> new RuntimeException("Room not found"));
        
    //     room.setName(roomDetails.getName());
    //     room.setCapacity(roomDetails.getCapacity());
    //     room.setLocation(roomDetails.getLocation());
    //     room.setDescription(roomDetails.getDescription());
    //     room.setAvailableEquipment(roomDetails.getAvailableEquipment());
    //     room.setImageUrl(roomDetails.getImageUrl());
    //     room.setRoomType(roomDetails.getRoomType());
        
    //     return roomRepository.save(room);
    // }

    // public void deleteRoom(Long id) {
    //     Room room = roomRepository.findById(id)
    //             .orElseThrow(() -> new RuntimeException("Room not found"));
    //     roomRepository.delete(room);
    // }
} 