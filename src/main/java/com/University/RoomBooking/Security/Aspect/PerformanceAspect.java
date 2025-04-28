package com.University.RoomBooking.Security.Aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    @Around("execution(* com.University.RoomBooking.Controller..*(..)) || execution(* com.University.RoomBooking.Service..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed(); // شغل الميثود
        long end = System.currentTimeMillis();
        long duration = end - start;

        log.info("[PERFORMANCE] {} executed in {} ms", joinPoint.getSignature(), duration);

        return result;
    }
}
