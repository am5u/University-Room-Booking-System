package com.University.RoomBooking.Security.Aspect;

import com.University.RoomBooking.Model.AuditTrail;
import com.University.RoomBooking.Repository.AuditTrailRepository;
import com.University.RoomBooking.Security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditTrailAspect {

    private final HttpServletRequest request;
    private final AuditTrailRepository auditTrailRepository;
    private final JwtService jwtService;

    @Pointcut("@annotation(com.University.RoomBooking.Security.Annotation.AuditAction)")
    public void auditActionMethods() {}

    @AfterReturning("auditActionMethods()")
    public void logAuditAction(JoinPoint joinPoint) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7);
        String userId = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
        String action = joinPoint.getSignature().getName();

        AuditTrail auditTrail = AuditTrail.builder()
                .userId(userId)
                .action(action)
                .timestamp(LocalDateTime.now())
                .build();

        auditTrailRepository.save(auditTrail);
    }
}
