package com.University.RoomBooking.Service;

import com.University.RoomBooking.Model.Role;
import com.University.RoomBooking.Model.User;
import com.University.RoomBooking.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> getUsersByRole(Role role) {
        log.info("Fetching users with role: {}", role);
        return userRepository.findByRole(role);
    }

    @Transactional
    public User updateUserRole(Long userId, Role newRole) {
        log.info("Updating role for user {} to {}", userId, newRole);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        log.info("Successfully updated role for user {} to {}", userId, newRole);
        return updatedUser;
    }

    @Transactional(readOnly = true)
    public boolean isUserAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRole() == Role.ADMIN;
    }

    @Transactional(readOnly = true)
    public boolean isUserStudent(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRole() == Role.STUDENT;
    }
} 