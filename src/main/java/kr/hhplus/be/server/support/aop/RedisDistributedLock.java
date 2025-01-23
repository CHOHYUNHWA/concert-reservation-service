package kr.hhplus.be.server.support.aop;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisDistributedLock {

    String key(); // Lock의 이름 (고유값)
    long waitTime() default 10L; // Lock 획득을 시도하는 최대 시간 (ms)
    long leaseTime() default 2L; // 락을 획득한 후, 점유하는 최대 시간 (ms)
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
