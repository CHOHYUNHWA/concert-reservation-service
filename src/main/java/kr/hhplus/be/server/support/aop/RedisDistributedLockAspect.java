package kr.hhplus.be.server.support.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(HIGHEST_PRECEDENCE)
public class RedisDistributedLockAspect {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
//    private final AopForTransaction aopForTransaction;

    @Around("@annotation(kr.hhplus.be.server.support.aop.RedisDistributedLock)")
    public Object redisDistributedLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisDistributedLock redisDistributedLock = method.getAnnotation(RedisDistributedLock.class);

        String key = REDISSON_LOCK_PREFIX +
                CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), redisDistributedLock.key());
        RLock rLock = redissonClient.getLock(key);

        try{
            boolean available = rLock.tryLock(redisDistributedLock.waitTime(), redisDistributedLock.leaseTime(), redisDistributedLock.timeUnit());

            if(!available){
                log.info("Lock 획득 실패 = {}", key);
                return false;
            }
            log.info("Lock 획득 성공 = {}", key);
            return joinPoint.proceed();
        } catch (InterruptedException e){
            throw new InterruptedException();
        } finally {
            try {
                log.info("Lock 해제 성공 = {}", key);
                rLock.unlock();
            } catch (IllegalMonitorStateException e){
                log.error("Redisson Lock Already UnLock ServiceName: {}, key: {}", method.getName(), key);
            }
        }
    }
}
