package com.yc.springframework.stereotype;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface MyPreDestroy {
}
