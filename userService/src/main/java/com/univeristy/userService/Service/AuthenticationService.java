package com.univeristy.userService.Service;      

import com.univeristy.userService.Dto.AuthenticationRequest;
import com.univeristy.userService.Dto.AuthenticationResponse;
import com.univeristy.userService.Dto.RegisterRequest;
import com.univeristy.userService.Model.*;
import com.univeristy.userService.Repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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


        User user = User.builder()
                .name(request.getName())
                .emailAddress(request.getEmailAddress())
                .password(passwordEncoder.encode(request.getPassword())) // Hash the password
                .role(Role.STUDENT)
                .department(request.getDepartment())
                .build();

       
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        
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
            User user = userRepository.findByEmailAddress(request.getEmailAddress())
                    .orElseThrow(() -> {
                        log.error("User not found: {}", request.getEmailAddress());
                        return new RuntimeException("User not found");
                    });

           
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.error("Authentication failed - Invalid password for user: {}", request.getEmailAddress());
                throw new RuntimeException("Invalid email or password");
            }

            log.info("Authentication successful for user: {}", request.getEmailAddress());
            log.debug("Found user in database: {}", user.getEmailAddress());
            log.debug("User role: {}", user.getRole());

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

   
}
