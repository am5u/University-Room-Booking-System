package com.University.RoomBooking.Security.Aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import com.University.RoomBooking.Security.JwtUtils;
import com.University.RoomBooking.Security.Annotation.AuditAction; 

import java.time.LocalDateTime;

@Aspect
@Component

@Slf4j
@RequiredArgsConstructor

public class AuditTrailAspect {

    private final HttpServletRequest request;
    private final JwtUtils jwtUtil; 

    @Pointcut("@annotation(auditAction)")
    public void auditPointcut(AuditAction auditAction) {}

    @AfterReturning(pointcut = "auditPointcut(auditAction)", returning = "result")
    public void auditAction(JoinPoint joinPoint, AuditAction auditAction, Object result) {
        try {
            String authHeader = request.getHeader("Authorization");
            String username = "anonymous";
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                username = jwtUtil.extractUsername(token);
            }

            String action = auditAction.value();
            String methodName = joinPoint.getSignature().getName();
            LocalDateTime now = LocalDateTime.now();

            log.info("[AUDIT] User: {} - Action: {} - Method: {} - Time: {}", username, action, methodName, now);
        } catch (Exception e) {
            log.error("[AUDIT-ERROR] {}", e.getMessage());
        }
    }
}
