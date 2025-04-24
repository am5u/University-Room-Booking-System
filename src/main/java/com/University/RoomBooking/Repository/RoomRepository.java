package com.University.RoomBooking.Repository;

import com.University.RoomBooking.Model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    // Find rooms by type
    // List<Room> findByType(String type);
    
    // Find rooms by capacity range
    // @Query("SELECT r FROM Room r WHERE r.capacity >= :minCapacity AND r.capacity <= :maxCapacity")
    // List<Room> findByCapacityRange(@Param("minCapacity") int minCapacity, @Param("maxCapacity") int maxCapacity);
    
    // // Find rooms by type and capacity range
    // @Query("SELECT r FROM Room r WHERE r.type = :type AND r.capacity >= :minCapacity AND r.capacity <= :maxCapacity")
    // List<Room> findByTypeAndCapacityRange(
    //     @Param("type") String type,
    //     @Param("minCapacity") int minCapacity,
    //     @Param("maxCapacity") int maxCapacity
    // );
    
    // Find rooms by features (using LIKE for partial matches)
    // @Query("SELECT r FROM Room r WHERE r.availableEquipment LIKE %:feature%")
    // List<Room> findByFeature(@Param("feature") String feature);
    
    // Find rooms by multiple features
    // @Query("SELECT r FROM Room r WHERE r.availableEquipment LIKE %:feature1% AND r.availableEquipment LIKE %:feature2%")
    // List<Room> findByMultipleFeatures(
    //     @Param("feature1") String feature1,
    //     @Param("feature2") String feature2
    // );
    
    // Find available rooms (not booked) for a specific time period
    @Query("SELECT DISTINCT r FROM Room r WHERE NOT EXISTS (" +
           "SELECT b FROM Booking b WHERE b.room = r " +
           "AND b.status = 'CONFIRMED' " +
           "AND ((b.startTime <= :endTime AND b.endTime >= :startTime) " +
           "OR (b.startTime >= :startTime AND b.startTime < :endTime) " +
           "OR (b.endTime > :startTime AND b.endTime <= :endTime)))")
    List<Room> findAvailableRooms(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}
    
    // Find rooms by type, capacity range, and features
//     @Query("SELECT r FROM Room r WHERE r.type = :type " +
//            "AND r.capacity >= :minCapacity AND r.capacity <= :maxCapacity " +
//            "AND r.availableEquipment LIKE %:feature%")
//     List<Room> findByTypeCapacityAndFeature(
//         @Param("type") String type,
//         @Param("minCapacity") int minCapacity,
//         @Param("maxCapacity") int maxCapacity,
//         @Param("feature") String feature
//     );
// } 