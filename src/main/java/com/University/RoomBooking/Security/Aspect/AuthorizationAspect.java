package com.University.RoomBooking.Security.Aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import com.University.RoomBooking.Security.JwtUtils; 

import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor

public class AuthorizationAspect {

    private final HttpServletRequest request;
    private final JwtUtils jwtUtil;

    @Pointcut("@annotation(com.University.RoomBooking.Security.AdminOnly)")
    public void adminOnlyMethods() {}

    @Before("adminOnlyMethods()")
    public void authorizeAdmin() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized: Missing Token");
        }

        String token = authHeader.substring(7);
        String role = jwtUtil.extractRole(token); 

        if (!Objects.equals(role, "ADMIN")) {
            throw new RuntimeException("Forbidden: Admins only");
        }
    }
}
