package com.University.RoomBooking.Repository;

import com.University.RoomBooking.Model.FacultyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacultyMemberRepository extends JpaRepository<FacultyMember, Long> {
    Optional<FacultyMember> findByUser_Id(Long userId);
} 