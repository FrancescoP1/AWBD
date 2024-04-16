package com.fmi.eduhub.logger;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

@Slf4j
@Aspect
@AllArgsConstructor
@Component
public class LoggerAspect {

  @Pointcut(value = "execution (* com.fmi.eduhub.service.*.*(..))")
  public void serviceMethod() {}

  @Pointcut(value = "execution (* com.fmi.eduhub.authentication.*.*(..))")
  public void authenticationMethod() {}

  @Around(value = "serviceMethod() || authenticationMethod()")
  public Object logMethodExecutionDetails(
      ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
    // before method execution
    String methodDeclaringType = proceedingJoinPoint.getSignature().getDeclaringTypeName();
    String methodName = proceedingJoinPoint.getSignature().getName();
    if(log.isDebugEnabled()) {
      log.debug("- Starting {}.{}() with argument(s) = {}",
          methodDeclaringType,
          methodName,
          Arrays.toString(proceedingJoinPoint.getArgs()));
    } else {
      log.info("- {}.{}() method execution started", methodDeclaringType, methodName);
    }
    Instant start = Instant.now();
    try {
      Object result = proceedingJoinPoint.proceed();
      // after method execution .proceed() lets the method execution begin
      Instant end = Instant.now();
      if(log.isDebugEnabled()) {
        log.debug(
            "- Finished: {}.{}() with argument(s) = {} in {} ms.",
            methodDeclaringType,
            methodName,
            Arrays.toString(proceedingJoinPoint.getArgs()),
            Duration.between(start, end).toMillis()
        );
      } else {
        log.info(
            "- {}.{}() method execution ended in {} ms.",
            methodDeclaringType,
            methodName,
            Duration.between(start, end).toMillis()
        );
      }
      return result;
    } catch(IllegalArgumentException exception) {
      log.error(
          "- Illegal argument: {} in {}.{}()",
          Arrays.toString(proceedingJoinPoint.getArgs()),
          methodDeclaringType,
          methodName
      );
      throw exception;
    }
  }

  @AfterThrowing(
      value = "serviceMethod()",
      throwing = "exception")
  public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
    String methodDeclaringType = joinPoint.getSignature().getDeclaringTypeName();
    String methodName = joinPoint.getSignature().getName();
    log.error("- EXCEPTION {} in {}.{}() caused by: {}",
        exception.getClass().getCanonicalName(),
        methodDeclaringType,
        methodName,
        exception.getCause() != null ? exception.getCause().toString() : "NULL");
    log.error("- EXCEPTION {} in {}.{}() caused by: {}",
        exception.getClass().getCanonicalName(),
        methodDeclaringType,
        methodName,
        exception.getMessage() != null ? exception.getMessage() : "NULL");
  }

  public static void logException(Throwable throwable, String methodPackage, String methodName) {
    log.error("- EXCEPTION {} in {}.{}() caused by: {}",
        throwable.getClass().getCanonicalName(),
        methodPackage,
        methodName,
        throwable.getCause() != null ? throwable.getCause().toString() : "NULL");
    log.error("- EXCEPTION {} in {}.{}() caused by: {}",
        throwable.getClass().getCanonicalName(),
        methodPackage,
        methodName,
        throwable.getMessage() != null ? throwable.getMessage() : "NULL");
  }
}
