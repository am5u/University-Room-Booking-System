package com.University.RoomBooking.Security.Annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditAction {
    String value() default "";
}
