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
public class RegisterRequest {
    private String name;
    private String emailAddress;
    private String password;
    private UserType userType;
    private String department;
    
    // Student specific fields
    private String studentId;
    private String program;
    private Integer yearOfStudy;
    
    // Faculty specific fields
    private String designation;
}
