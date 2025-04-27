package com.University.RoomBooking.Repository;

import com.University.RoomBooking.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoomId(Long roomId);
    List<Booking> findByUser_Id(Long userId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND " +
           "((b.startTime <= :endTime AND b.endTime >= :startTime) OR " +
           "(b.startTime >= :startTime AND b.startTime < :endTime) OR " +
           "(b.endTime > :startTime AND b.endTime <= :endTime)) AND " +
           "(b.status = 'CONFIRMED' OR b.status = 'PENDING')")
    List<Booking> findConflictingBookingsWithLock(
        @Param("roomId") Long roomId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
} 