package com.univeristy.bookingService.Service;

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
    public Booking createBooking(Long roomId, Long userId, String date, String time, int duration, String purpose) {
        // Parse date and time
        LocalDate bookingDate;
        LocalTime bookingTime;
        try {
            bookingDate = LocalDate.parse(date);
            bookingTime = LocalTime.parse(time);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date or time format");
        }

        // Create start and end time
        LocalDateTime startTime = LocalDateTime.of(bookingDate, bookingTime);
        LocalDateTime endTime = startTime.plusHours(duration);

        // Validate booking time is in the future
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Booking time must be in the future");
        }

        // Get room
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // We don't need to validate the user here since that's handled by the userService
        // We just use the userId directly

        // Check for conflicting bookings with pessimistic locking
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookingsWithLock(roomId, startTime, endTime);
        if (!conflictingBookings.isEmpty()) {
            throw new IllegalArgumentException("Room is not available for the selected time period");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setUserId(userId);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setPurpose(purpose);
        booking.setStatus("PENDING");

        try {
            return bookingRepository.save(booking);
        } catch (OptimisticLockException e) {
            throw new IllegalArgumentException("Booking failed due to concurrent modification. Please try again.");
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to save booking: " + e.getMessage());
        }
    }



    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Check if booking is already cancelled
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        // Check if booking is in the past
        if (booking.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot cancel a booking that has already started");
        }

        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }


    @Transactional
    public String rejectBooking(Long bookingId) {
        // Find the booking by ID
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking with ID " + bookingId + " not found"));

        // Update the booking status to "CANCELLED"
        if (booking.getStatus().equals("REJECTED")) {
            throw new IllegalArgumentException("Booking is already REJECTED");
        }

        booking.setStatus("REJECTED");

        bookingRepository.save(booking);

        return "Booking with ID " + bookingId + " has been rejected";
    }


    @Transactional
    public void acceptBooking(Long Id) {
        Booking booking = bookingRepository.findById(Id)
            .orElseThrow(() -> new IllegalArgumentException("booking not found"));

        // Update the status to "CONFIRMED"
        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}