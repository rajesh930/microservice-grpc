package com.spr.microservice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 12:06 PM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MicroServiceMethod {
    String name() default "";

    boolean async() default false;

    long timeout() default 5 * 60 * 1000;

    int retries() default 3;
}
