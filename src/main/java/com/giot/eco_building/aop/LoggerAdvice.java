package com.giot.eco_building.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: pyt
 * @Date: 2020/6/11 10:57
 * @Description:
 */
@Aspect
@Component
public class LoggerAdvice {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(public * com.giot.eco_building.controller..*(..))")
    public void log() {
    }

    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        logger.info("开始调用接口,url={},method={},ip={},class_method={},args={}",
                request.getRequestURL(),
                request.getMethod(),
                request.getRemoteAddr(),
                joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }

    @AfterThrowing(pointcut = "log()", throwing = "ex")
    public void doAfterThrowException(JoinPoint joinPoint, Exception ex) {
        logger.info("接口:{}调用异常,exception={}",
                joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(),
                ex);
    }

    @AfterReturning(returning = "object", pointcut = "log()")
    public void doAfterReturning(JoinPoint joinPoint, Object object) {
        logger.info("接口:{}调用结束,response={}",
                joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(),
                object);
    }
}
