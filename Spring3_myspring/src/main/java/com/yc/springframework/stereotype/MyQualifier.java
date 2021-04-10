package com.yc.springframework.stereotype;

import java.lang.annotation.*;

@Target({ElementType.CONSTRUCTOR,ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyQualifier {
}
