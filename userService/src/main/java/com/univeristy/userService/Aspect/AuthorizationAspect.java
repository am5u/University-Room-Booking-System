package com.univeristy.userService.Aspect;  

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final HttpServletRequest request;

    @Pointcut("@annotation(com.univeristy.userService.Annotation.AdminOnly)")
    public void adminOnlyMethods() {}

    @Before("adminOnlyMethods()")
    public void authorizeAdmin() {
        String role = request.getHeader("X-User-Role"); // Read role from custom header

        if (role == null || role.isBlank()) {
            throw new RuntimeException("Unauthorized: No role provided");
        }

        if (!Objects.equals(role, "ROLE_ADMIN")) {
            throw new RuntimeException("Forbidden: Admins only");
        }
    }
}
