package com.univeristy.bookingService.Dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long roomId;
    private Long userId;
    private String date;
    private String time;
    private Integer duration;
    private String purpose;
} 