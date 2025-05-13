package com.univeristy.userService.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Booking {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private String status;
    private Long roomId;
    private Long userId;

    // Direct reference to Room and User objects
    private Room room;

    
    // For frontend compatibility
    public Long getBookingId() {
        return id;
    }

    public void setBookingId(Long bookingId) {
        this.id = bookingId;
    }

    // These fields will be populated by the service
    private String roomName;
    private String userName;
    private String userDepartment;
    private Role userRole;

    // Mock user object for frontend compatibility
    public User getUser() {
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setName(userName != null ? userName : "Unknown User");
        mockUser.setRole(userRole);
        mockUser.setDepartment(userDepartment != null ? userDepartment : "Unknown Department");
        return mockUser;
    }

    // Return the actual room object if available, otherwise create a mock
    public Room getRoom() {
        // If we have a direct room reference from the API, use it
        if (room != null) {
            return room;
        }

        // Otherwise, create a mock room with the available information
        Room mockRoom = new Room();
        mockRoom.setId(roomId);
        mockRoom.setName(roomName != null ? roomName : "Unknown Room");
        return mockRoom;
    }
}
