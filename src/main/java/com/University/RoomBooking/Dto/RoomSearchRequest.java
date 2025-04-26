package com.University.RoomBooking.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomSearchRequest {
    private String date;
    private String time;
    private Integer duration; // Duration in hours
    private Integer capacity;
    private String roomType;
} 