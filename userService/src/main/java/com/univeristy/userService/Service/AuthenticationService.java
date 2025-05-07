package com.univeristy.userService.Service;      

import com.univeristy.userService.Dto.AuthenticationRequest;
import com.univeristy.userService.Dto.AuthenticationResponse;
import com.univeristy.userService.Dto.RegisterRequest;
import com.univeristy.userService.Model.*;
import com.univeristy.userService.Repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUserByEmailAddress(String emailAddress) {
        log.info("Fetching user information for email: {}", emailAddress);
        return userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", emailAddress);
                    return new RuntimeException("User not found");
                });
    }

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        log.info("Starting user registration for name: {}", request.getName());

        // Check if the email already exists
        if (userRepository.findByEmailAddress(request.getEmailAddress()).isPresent()) {
            log.error("Registration failed - Email already exists: {}", request.getEmailAddress());
            throw new RuntimeException("Email already exists");
        }

        // Create a new User object with plain password (no security)
        User user = User.builder()
                .name(request.getName())
                .emailAddress(request.getEmailAddress())
                .password(request.getPassword()) // Store password as plain text since we removed security
                .role(Role.STUDENT)
                .department(request.getDepartment())
                .build();

        // Save the user to the database
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Return the authentication response
        return AuthenticationResponse.builder()
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .role(savedUser.getRole().name())
                .message("Registration successful")
                .build();
    }



    @Transactional(readOnly = true)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Starting authentication for email: {}", request.getEmailAddress());

        try {
            // Retrieve the user from the database
            User user = userRepository.findByEmailAddress(request.getEmailAddress())
                    .orElseThrow(() -> {
                        log.error("User not found: {}", request.getEmailAddress());
                        return new RuntimeException("User not found");
                    });

            // Simple password check (no encryption since we removed security)
            if (!user.getPassword().equals(request.getPassword())) {
                log.error("Authentication failed - Invalid password for user: {}", request.getEmailAddress());
                throw new RuntimeException("Invalid email or password");
            }

            log.info("Authentication successful for user: {}", request.getEmailAddress());
            log.debug("Found user in database: {}", user.getEmailAddress());
            log.debug("User role: {}", user.getRole());

            // Return the authentication response
            return AuthenticationResponse.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .role(user.getRole().name())
                    .message("Authentication successful")
                    .build();

        } catch (RuntimeException e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw new RuntimeException("Invalid email or password");
        }
    }

    // Debug method to check all users in database
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Found {} users in database", users.size());
        users.forEach(user -> {
            log.info("User: {}", user.getName());
            log.info("Role: {}", user.getRole());
            log.info("Email: {}", user.getEmailAddress());
        });
        return users;
    }
}
