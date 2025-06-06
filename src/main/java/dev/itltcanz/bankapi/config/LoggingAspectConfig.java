package dev.itltcanz.bankapi.config;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;


/**
 * Aspect for logging method execution details in the service layer.
 */
@Aspect
@Slf4j
@Component
public class LoggingAspectConfig {

  /**
   * Logs method entry with class, method name, and arguments.
   *
   * @param joinPoint the join point providing method information
   */
  @Before("execution(* dev.itltcanz.bankapi.service.*.*(..))")
  public void logMethodEntry(JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().getName();
    String className =
        joinPoint.getTarget() != null ? joinPoint.getTarget().getClass().getSimpleName()
            : "Unknown";
    Object[] args = joinPoint.getArgs();
    log.info("Entering method: {}.{} with arguments: {}", className, methodName,
        Arrays.toString(args));
  }

  /**
   * Logs method exit with class, method name, and return value.
   *
   * @param joinPoint the join point providing method information
   * @param result    the result returned by the method
   */
  @AfterReturning(pointcut = "execution(* dev.itltcanz.bankapi.service.*.*(..))", returning = "result")
  public void logMethodExit(JoinPoint joinPoint, Object result) {
    String methodName = joinPoint.getSignature().getName();
    String className =
        joinPoint.getTarget() != null ? joinPoint.getTarget().getClass().getSimpleName()
            : "Unknown";
    log.info("Exiting method: {}.{} with result: {}", className, methodName,
        result != null ? result.toString() : "null");
  }

  /**
   * Logs exceptions thrown by methods.
   *
   * @param joinPoint the join point providing method information
   * @param exception the exception thrown by the method
   */
  @AfterThrowing(pointcut = "execution(* dev.itltcanz.bankapi.service.*.*(..))", throwing = "exception")
  public void logMethodException(JoinPoint joinPoint, Exception exception) {
    String methodName = joinPoint.getSignature().getName();
    String className =
        joinPoint.getTarget() != null ? joinPoint.getTarget().getClass().getSimpleName()
            : "Unknown";
    log.error("Exception in method: {}.{}, error: {}", className, methodName,
        exception.getMessage());
  }
}