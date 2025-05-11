package com.univeristy.bookingService.Service;

import com.univeristy.bookingService.Annotation.LogExecutionTime;
import com.univeristy.bookingService.Model.Booking;
import com.univeristy.bookingService.Model.Room;
import com.univeristy.bookingService.Repository.BookingRepository;
import com.univeristy.bookingService.Repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.OptimisticLockException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    @Transactional
    @LogExecutionTime
    public Booking createBooking(Long roomId, Long userId, String date, String time, int duration, String purpose) {
        // Basic validation
        if (roomId == null || userId == null || date == null || time == null || purpose == null) {
            throw new IllegalArgumentException("All fields are required");
        }

        LocalDateTime startTime;
        try {
            LocalDate bookingDate = LocalDate.parse(date);
            LocalTime bookingTime = LocalTime.parse(time);
            startTime = LocalDateTime.of(bookingDate, bookingTime);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date or time format");
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Booking time must be in the future");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        LocalDateTime endTime = startTime.plusHours(duration);
        if (!bookingRepository.findConflictingBookingsWithLock(roomId, startTime, endTime).isEmpty()) {
            throw new IllegalArgumentException("Room is not available for the selected time period");
        }

        // Create and save booking
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setUserId(userId);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setPurpose(purpose);
        booking.setStatus("PENDING");

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Transactional
    @LogExecutionTime
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        if (booking.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot cancel a booking that has already started");
        }

        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    @Transactional
    @LogExecutionTime
    public String rejectBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if ("REJECTED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Booking is already rejected");
        }

        booking.setStatus("REJECTED");
        bookingRepository.save(booking);
        return "Booking with ID " + bookingId + " has been rejected";
    }

    @Transactional
    @LogExecutionTime
    public void acceptBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}