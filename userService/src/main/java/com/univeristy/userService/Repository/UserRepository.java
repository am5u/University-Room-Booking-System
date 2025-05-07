package com.univeristy.userService.Repository;      

import com.univeristy.userService.Model.Role;
import com.univeristy.userService.Model.User;
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
