package com.University.RoomBooking.Service;

import com.University.RoomBooking.Dto.AuthenticationRequest;
import com.University.RoomBooking.Dto.AuthenticationResponse;
import com.University.RoomBooking.Dto.RegisterRequest;
import com.University.RoomBooking.Model.*;
import com.University.RoomBooking.Repository.*;
import com.University.RoomBooking.Security.JwtService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyMemberRepository facultyMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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

        if (userRepository.findByEmailAddress(request.getEmailAddress()).isPresent()) {
            log.error("Registration failed - Email already exists: {}", request.getEmailAddress());
            throw new RuntimeException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        log.debug("Password encoded successfully");

        User user = User.builder()
                .name(request.getName())
                .emailAddress(request.getEmailAddress())
                .password(encodedPassword)
                .role(Role.USER)
                .userType(request.getUserType())
                .department(request.getDepartment())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Create student or faculty member record based on user type
        if (request.getUserType() == UserType.STUDENT) {
            Student student = Student.builder()
                    .user(savedUser)
                    .studentId(request.getStudentId())
                    .program(request.getProgram())
                    .yearOfStudy(request.getYearOfStudy())
                    .build();
            studentRepository.save(student);
            log.info("Student record created for user: {}", savedUser.getId());
        } else if (request.getUserType() == UserType.FACULTY_MEMBER) {
            FacultyMember facultyMember = FacultyMember.builder()
                    .user(savedUser)
                    .designation(request.getDesignation())
                    .build();
            facultyMemberRepository.save(facultyMember);
            log.info("Faculty member record created for user: {}", savedUser.getId());
        }

        String jwtToken = jwtService.generateToken(savedUser);
        log.info("JWT token generated for user: {}", savedUser.getName());

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Starting authentication for email: {}", request.getEmailAddress());

        try {
            log.debug("Attempting to authenticate user with email: {}", request.getEmailAddress());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmailAddress(),
                            request.getPassword()
                    )
            );

            log.info("Authentication successful for user: {}", request.getEmailAddress());

            User user = userRepository.findByEmailAddress(request.getEmailAddress())
                    .orElseThrow(() -> {
                        log.error("User not found after successful authentication: {}", request.getEmailAddress());
                        return new RuntimeException("User not found");
                    });

            log.debug("Found user in database: {}", user.getEmailAddress());
            log.debug("User role: {}", user.getRole());
            log.debug("User type: {}", user.getUserType());

            String jwtToken = jwtService.generateToken(user);
            log.info("JWT token generated for authenticated user: {}", user.getName());

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();

        } catch (BadCredentialsException e) {
            log.error("Authentication failed - Invalid credentials for user: {}", request.getEmailAddress());
            throw new RuntimeException("Invalid email or password");
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {} - Error: {}", request.getEmailAddress(), e.getMessage());
            log.error("Authentication exception details: ", e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
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
            log.info("Password Hash: {}", user.getPassword());
            log.info("Email: {}", user.getEmailAddress());
        });
        return users;
    }
}
