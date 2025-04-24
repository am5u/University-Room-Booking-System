package com.University.RoomBooking.Repository;

import com.University.RoomBooking.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoomId(Long roomId);
    List<Booking> findByUser_Id(Long userId);
} 