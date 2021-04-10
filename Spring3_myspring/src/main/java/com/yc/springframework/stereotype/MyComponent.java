package com.yc.springframework.stereotype;

/**
 * @program: TestSpring
 * @description:
 * @author: 作者
 * @create: 2021-04-05 15:28
 */
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyComponent {
    String value() default "" ;
}
