package com.University.RoomBooking.Controller;

import org.springframework.web.bind.annotation.*;

import com.University.RoomBooking.Service.BookingService;

import lombok.RequiredArgsConstructor;

import com.University.RoomBooking.Security.Annotation.AdminOnly;
import com.University.RoomBooking.Security.Annotation.AuditAction;

@RestController


@RequestMapping("api/admin")
@RequiredArgsConstructor


public class AdminController {

    private final BookingService bookingService;

    @AdminOnly
    @GetMapping("/dashboard")
    public String getDashboard() {
        return "Welcome to the Admin Dashboard";
    }

    @AdminOnly
    @GetMapping("/allbookings")
    public String getAllBookings() {
        return bookingService.getAllBookings().toString();
    }

    @AdminOnly
    @AuditAction("Accept Booking")
    @PostMapping("/acceptbooking")
    public String acceptBooking(@RequestParam int Id) {
        bookingService.acceptBooking((long)Id);
        return "Reservation with ID " + Id + " has been accepted";
    }

    @AdminOnly
    @PostMapping("/rejectbooking")
    @AuditAction("Reject Booking")
    @ResponseBody
    public String rejectBooking(@RequestParam Long Id) {
        return bookingService.rejectBooking(Id);
    }
}