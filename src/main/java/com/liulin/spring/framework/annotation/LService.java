package com.liulin.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * Create by DbL on 2020/4/29 0029
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LService {
    String value() default "";
}
