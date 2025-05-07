package com.univeristy.userService.Service;

import com.univeristy.userService.Model.Booking;
import com.univeristy.userService.Model.Role;
import com.univeristy.userService.Model.Room;
import com.univeristy.userService.Model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;


@Service
@Slf4j
public class BookingService {

    private final String BOOKING_SERVICE_URL = "http://localhost:8080/api/bookings";

    @Autowired
    private RestTemplate restTemplate;

    public BookingService() {
        this.restTemplate = new RestTemplate();
    }

    public List<Booking> getAllBookings() {
        try {
            System.out.println("BookingService: Fetching all bookings from " + BOOKING_SERVICE_URL + "/all");
            ResponseEntity<List<Booking>> response = restTemplate.exchange(
                BOOKING_SERVICE_URL + "/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Booking>>() {}
            );

            List<Booking> bookings = response.getBody();
            System.out.println("BookingService: Received " + (bookings != null ? bookings.size() : 0) + " bookings from booking service");

            if (bookings != null) {
                // Enhance bookings with user and room names
                for (Booking booking : bookings) {
                    System.out.println("BookingService: Processing booking ID: " + booking.getId() +
                                      ", User ID: " + booking.getUserId() +
                                      ", Room ID: " + booking.getRoomId());
                    try {
                        // Get user info if userId is available
                        if (booking.getUserId() != null) {
                            try {
                                String userUrl = "http://localhost:8080/api/auth/user/" + booking.getUserId();
                                System.out.println("BookingService: Fetching user info from " + userUrl);

                                ResponseEntity<User> userResponse = restTemplate.getForEntity(
                                    userUrl,
                                    User.class
                                );

                                System.out.println("BookingService: User API response status: " + userResponse.getStatusCode());

                                if (userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null) {
                                    User user = userResponse.getBody();
                                    System.out.println("BookingService: Retrieved user: " + user.getName() +
                                                     ", ID: " + user.getId() +
                                                     ", Department: " + user.getDepartment() +
                                                     ", Role: " + user.getRole());

                                    // Set user information in the booking object
                                    booking.setUserName(user.getName());

                                    // Add these fields to the Booking class
                                    try {
                                        java.lang.reflect.Method setDepartmentMethod = booking.getClass().getMethod("setUserDepartment", String.class);
                                        setDepartmentMethod.invoke(booking, user.getDepartment());
                                        System.out.println("BookingService: Set user department to: " + user.getDepartment());
                                    } catch (Exception e) {
                                        System.out.println("BookingService: Could not set user department: " + e.getMessage());
                                    }

                                    try {
                                        java.lang.reflect.Method setRoleMethod = booking.getClass().getMethod("setUserRole", Role.class);
                                        setRoleMethod.invoke(booking, user.getRole());
                                        System.out.println("BookingService: Set user role to: " + user.getRole());
                                    } catch (Exception e) {
                                        System.out.println("BookingService: Could not set user role: " + e.getMessage());
                                    }
                                } else {
                                    System.out.println("BookingService: User not found or response empty");
                                    booking.setUserName("Unknown User");
                                }
                            } catch (Exception ex) {
                                System.out.println("BookingService: Error fetching user info: " + ex.getMessage());
                                log.error("Error fetching user info: {}", ex.getMessage());
                                booking.setUserName("Unknown User");
                            }
                        } else {
                            System.out.println("BookingService: User ID is null");
                            booking.setUserName("Unknown User");
                        }

                        // Check if we already have room information
                        if (booking.getRoom() != null) {
                            System.out.println("BookingService: Booking already has room information: " + booking.getRoom());

                            // Set roomName for backward compatibility
                            if (booking.getRoom().getName() != null) {
                                booking.setRoomName(booking.getRoom().getName());
                                System.out.println("BookingService: Set roomName to: " + booking.getRoom().getName());
                            } else {
                                booking.setRoomName("Room " + booking.getRoomId());
                                System.out.println("BookingService: Room name is null, using Room ID: " + booking.getRoomId());
                            }
                        } else if (booking.getRoomId() != null) {
                            // If we don't have room information but have roomId, try to fetch it
                            try {
                                String roomUrl = "http://localhost:8080/api/rooms/" + booking.getRoomId();
                                System.out.println("BookingService: Fetching room info from " + roomUrl);

                                ResponseEntity<Room> roomResponse = restTemplate.getForEntity(
                                    roomUrl,
                                    Room.class
                                );

                                System.out.println("BookingService: Room API response status: " + roomResponse.getStatusCode());

                                if (roomResponse.getStatusCode().is2xxSuccessful() && roomResponse.getBody() != null) {
                                    Room room = roomResponse.getBody();
                                    System.out.println("BookingService: Retrieved room: " + room);

                                    // Set the room object
                                    booking.setRoom(room);

                                    // Also set roomName for backward compatibility
                                    if (room.getName() != null) {
                                        System.out.println("BookingService: Retrieved room name: " + room.getName());
                                        booking.setRoomName(room.getName());
                                    } else {
                                        System.out.println("BookingService: Room name is null");
                                        booking.setRoomName("Room " + booking.getRoomId());
                                    }
                                } else {
                                    System.out.println("BookingService: Room not found or response empty");
                                    booking.setRoomName("Room " + booking.getRoomId());
                                }
                            } catch (Exception ex) {
                                System.out.println("BookingService: Error fetching room info: " + ex.getMessage());
                                log.error("Error fetching room info: {}", ex.getMessage());
                                booking.setRoomName("Room " + booking.getRoomId());
                            }
                        } else {
                            System.out.println("BookingService: Room ID is null");
                            booking.setRoomName("Unknown Room");
                        }

                    } catch (Exception e) {
                        log.error("Error enhancing booking with user/room info: {}", e.getMessage());
                        // Set default values if enhancement fails
                        booking.setUserName("Unknown User");
                        booking.setRoomName("Unknown Room");
                    }
                }
            }

            return bookings != null ? bookings : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching all bookings: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void acceptBooking(Long bookingId) {
        try {
            restTemplate.postForEntity(
                BOOKING_SERVICE_URL + "/" + bookingId + "/accept",
                null,
                String.class
            );
        } catch (Exception e) {
            log.error("Error accepting booking: {}", e.getMessage());
            throw new RuntimeException("Failed to accept booking: " + e.getMessage());
        }
    }

    public String rejectBooking(Long bookingId) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                BOOKING_SERVICE_URL + "/" + bookingId + "/reject",
                null,
                String.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error rejecting booking: {}", e.getMessage());
            throw new RuntimeException("Failed to reject booking: " + e.getMessage());
        }
    }
}
