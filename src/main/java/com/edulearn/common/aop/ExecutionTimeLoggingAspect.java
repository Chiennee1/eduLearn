package com.edulearn.common.aop;

import com.edulearn.common.config.ExecutionLogProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ExecutionTimeLoggingAspect {

    private final ExecutionLogProperties properties;

    @Around("execution(* com.edulearn..service..*(..)) || execution(* com.edulearn..controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!properties.isEnabled()) {
            return joinPoint.proceed();
        }

        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            String signature = joinPoint.getSignature().toShortString();
            if (durationMs >= properties.getWarnThresholdMs()) {
                log.warn("Slow execution: {} took {} ms", signature, durationMs);
            } else {
                log.info("Execution: {} took {} ms", signature, durationMs);
            }
        }
    }
}

