package com.University.RoomBooking.Repository;

import com.University.RoomBooking.Model.Role;
import com.University.RoomBooking.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAddress(String emailAddress);
    List<User> findByRole(Role role);
    boolean existsByEmailAddress(String emailAddress);
}
