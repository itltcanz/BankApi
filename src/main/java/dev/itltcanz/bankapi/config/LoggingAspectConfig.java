package dev.itltcanz.bankapi.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggingAspectConfig {

    @Before("execution(* dev.itltcanz.bankapi.service.*.*(..))")
    public void logMethodEntry(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        log.info("Entering method: {}.{} with arguments: {}", className, methodName, args);
    }

    @AfterReturning(pointcut = "execution(* dev.itltcanz.bankapi.service.*.*(..))", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.info("Exiting method: {}.{} with result: {}", className, methodName, result);
    }

    @AfterThrowing(pointcut = "execution(* dev.itltcanz.bankapi.service.*.*(..))", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Exception exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.error("Exception in method: {}.{}, error: {}", className, methodName, exception.getMessage());
    }
}