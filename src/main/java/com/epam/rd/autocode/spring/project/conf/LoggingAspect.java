package com.epam.rd.autocode.spring.project.conf;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.epam.rd.autocode.spring.project.service.impl..*) || " +
              "within(com.epam.rd.autocode.spring.project.controller..*)")
    public void applicationPackagePointcut() {
    }

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("Enter: {}.{}() with argument[s] = {}", className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - startTime;
            log.info("Exit: {}.{}() with result = {} (Execution time: {} ms)", className, methodName, result, elapsedTime);
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(args), className, methodName);
            throw e;
        } catch (Throwable e) {
            log.error("Exception in {}.{}() with message = {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}
