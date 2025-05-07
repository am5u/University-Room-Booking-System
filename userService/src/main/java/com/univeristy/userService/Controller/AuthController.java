package com.univeristy.userService.Controller;

import com.univeristy.userService.Dto.AuthenticationRequest;
import com.univeristy.userService.Dto.AuthenticationResponse;
import com.univeristy.userService.Dto.RegisterRequest;
import com.univeristy.userService.Model.User;
import com.univeristy.userService.Service.AuthenticationService;
import com.univeristy.userService.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



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
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/user/email/{emailAddress}")
    public ResponseEntity<User> getUserInfo(@PathVariable String emailAddress) {
        try {
            User user = authenticationService.getUserByEmailAddress(emailAddress);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        try {
            System.out.println("AuthController: Fetching user with ID: " + userId);
            User user = userService.getUserById(userId);
            System.out.println("AuthController: Found user: " + user.getName() + ", ID: " + user.getId());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            System.out.println("AuthController: Error finding user with ID " + userId + ": " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }



}
