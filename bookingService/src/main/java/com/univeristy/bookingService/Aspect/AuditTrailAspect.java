package com.univeristy.bookingService.Aspect;

import com.univeristy.bookingService.Model.AuditTrail;
import com.univeristy.bookingService.Repository.AuditTrailRepository;
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

    @Pointcut("@annotation(com.univeristy.bookingService.Annotation.AuditAction)")
    public void auditActionMethods() {}

    @AfterReturning("auditActionMethods()")
    public void logAuditAction(JoinPoint joinPoint) {
        // Try to get user ID from various sources
        String userId = null;

        // 1. Try to get from request header (X-User-ID) - This is the primary source from the frontend
        userId = request.getHeader("X-User-ID");

        // 2. Try to get from request attribute
        if (userId == null && request.getAttribute("userId") != null) {
            userId = request.getAttribute("userId").toString();
        }

        // 3. Try to get from request parameter
        if (userId == null) {
            userId = request.getParameter("userId");
        }

        // 4. Try to get from session
        if (userId == null && request.getSession().getAttribute("userId") != null) {
            userId = request.getSession().getAttribute("userId").toString();
        }

        // 5. Try to get from method parameters
        if (userId == null) {
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof Long) {
                    userId = arg.toString();
                    break;
                }
            }
        }

        // If still null, use "anonymous"
        if (userId == null) {
            userId = "anonymous";
        }

        // Log the user ID for debugging
        System.out.println("AuditTrail - User ID: " + userId);

        String action = joinPoint.getSignature().getName();

        AuditTrail auditTrail = AuditTrail.builder()
                .userId(userId)
                .action(action)
                .timestamp(LocalDateTime.now())
                .build();

        auditTrailRepository.save(auditTrail);
    }
}
