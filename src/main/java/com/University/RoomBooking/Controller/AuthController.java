package com.University.RoomBooking.Controller;

import com.University.RoomBooking.Dto.AuthenticationRequest;
import com.University.RoomBooking.Dto.AuthenticationResponse;
import com.University.RoomBooking.Dto.RegisterRequest;
import com.University.RoomBooking.Model.User;
import com.University.RoomBooking.Service.AuthenticationService;
import com.University.RoomBooking.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.register(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    AuthenticationResponse.builder()
                            .token(null)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    AuthenticationResponse.builder()
                            .token(null)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailAddress = authentication.getName();
        User user = authenticationService.getUserByEmailAddress(emailAddress);
        return ResponseEntity.ok(user);
    }

    // Admin endpoints
    
}
