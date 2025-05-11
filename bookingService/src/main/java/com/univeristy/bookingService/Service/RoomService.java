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
        LocalDateTime startTime = LocalDateTime.of(
            LocalDate.parse(request.getDate()),
            LocalTime.parse(request.getTime())
        );
        LocalDateTime endTime = startTime.plusHours(request.getDuration());

        return roomRepository.findAvailableRooms(startTime, endTime).stream()
            .filter(room -> request.getCapacity() == null || room.getCapacity() >= request.getCapacity())
            .filter(room -> request.getRoomType() == null || 
                          request.getRoomType().trim().isEmpty() || 
                          room.getRoomType().equalsIgnoreCase(request.getRoomType().trim()))
            .toList();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public boolean checkRoomAvailability(Long roomId, String date, String time, int duration) {
        LocalDateTime startTime = LocalDateTime.of(
            LocalDate.parse(date),
            LocalTime.parse(time)
        );
        LocalDateTime endTime = startTime.plusHours(duration);

        return bookingRepository.findByRoomId(roomId).stream()
            .filter(booking -> booking.getStatus().equals("CONFIRMED") || booking.getStatus().equals("PENDING"))
            .noneMatch(booking -> isTimeOverlapping(booking, startTime, endTime));
    }

    private boolean isTimeOverlapping(Booking booking, LocalDateTime startTime, LocalDateTime endTime) {
        return (booking.getStartTime().isBefore(endTime) && booking.getEndTime().isAfter(startTime)) ||
               (booking.getStartTime().isAfter(startTime) && booking.getStartTime().isBefore(endTime)) ||
               (booking.getEndTime().isAfter(startTime) && booking.getEndTime().isBefore(endTime));
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