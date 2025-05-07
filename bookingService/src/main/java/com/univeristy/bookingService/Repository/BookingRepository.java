package com.univeristy.bookingService.Repository;

import com.univeristy.bookingService.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
        @Modifying
        @Transactional
        List<Booking> findByRoomId(Long roomId);

        List<Booking> findByUserId(Long userId);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND " +
                        "((b.startTime <= :endTime AND b.endTime >= :startTime) OR " +
                        "(b.startTime >= :startTime AND b.startTime < :endTime) OR " +
                        "(b.endTime > :startTime AND b.endTime <= :endTime)) AND " +
                        "(b.status = 'CONFIRMED' OR b.status = 'PENDING')")
        List<Booking> findConflictingBookingsWithLock(
                        @Param("roomId") Long roomId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);

        @Modifying
        @Transactional
        @Query("UPDATE Booking b SET b.status = 'CANCELLED' WHERE b.id = :Id")
        int rejectBooking(@Param("Id") Long Id);

        @Modifying
        @Transactional
        @Query("UPDATE Booking b SET b.status = 'CONFIRMED' WHERE b.id = :Id")
        int acceptBooking(@Param("Id") Long Id);

        @Query("FROM Booking")
        List<Booking> findAll();

        @Query("FROM Booking b WHERE b.status = 'PENDING'")
        List<Booking> findPendingBookings();

}