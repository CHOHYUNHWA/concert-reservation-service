package kr.hhplus.be.server.support.aop;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisDistributedLock {

    String key(); // Lock의 이름 (고유값)
    long waitTime() default 10L;
    long leaseTime() default 2L;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
