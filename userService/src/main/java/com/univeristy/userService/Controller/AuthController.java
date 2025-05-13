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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

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

    @PutMapping("/user/{userId}")
    public ResponseEntity<User> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request) {
        try {
            User user = userService.getUserById(userId);
            
            if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
                if (request.getCurrentPassword() == null || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null);
                }
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            }
            
            user.setName(request.getName());
            user.setDepartment(request.getDepartment());
            
            User updatedUser = userService.saveUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class UpdateProfileRequest {
    private String name;
    private String department;
    private String currentPassword;
    private String newPassword;
}
