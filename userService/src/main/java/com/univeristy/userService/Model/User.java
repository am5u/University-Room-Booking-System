package com.univeristy.userService.Model;      

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String emailAddress;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)

    @Column(nullable = false)
    //student by default

    @Builder.Default
    private Role role = Role.STUDENT;



    @Column(nullable = false)
    private String department;

    public String getUsername() {
        return emailAddress;
    }
}
