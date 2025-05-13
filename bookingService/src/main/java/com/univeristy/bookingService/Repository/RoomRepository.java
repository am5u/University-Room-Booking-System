package com.univeristy.bookingService.Repository;      

import com.univeristy.bookingService.Model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    @Query("SELECT DISTINCT r FROM Room r WHERE NOT EXISTS (" +
           "SELECT b FROM Booking b WHERE b.room = r " +
           "AND b.status NOT IN ('CANCELLED', 'REJECTED') " +
           "AND ((b.startTime <= :endTime AND b.endTime >= :startTime) " +
           "OR (b.startTime >= :startTime AND b.startTime < :endTime) " +
           "OR (b.endTime > :startTime AND b.endTime <= :endTime)))")
    List<Room> findAvailableRooms(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    List<Room> findByRoomType(String roomType);
}
    
 