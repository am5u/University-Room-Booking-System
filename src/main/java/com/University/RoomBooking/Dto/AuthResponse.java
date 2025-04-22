package com.University.RoomBooking.Dto;

import com.University.RoomBooking.Model.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    // Authentication fields
    private String token;
    private String message;
    
    // User information fields
    private String name;
    private String emailAddress;
    private UserType userType;
    private String department;
} 