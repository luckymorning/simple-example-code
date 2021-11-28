package com.cn.lucky.morning.limit.aop;

import com.cn.lucky.morning.limit.annotation.RequestLimit;
import com.cn.lucky.morning.limit.dto.RequestLimitDTO;
import com.cn.lucky.morning.limit.factory.RequestLimitFactory;
import com.cn.lucky.morning.limit.service.RequestLimitService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * <p>
 * 限流AOP
 * </p>
 *
 * @author wangchen
 * @since 2021/11/23
 */
@Aspect
@Component
public class RequestLimitAop {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLimitAop.class);

    @Autowired
    private RequestLimitFactory factory;

    /**
     * 切入点
     */
    @Pointcut(value = "@annotation(com.cn.lucky.morning.limit.annotation.RequestLimit)")
    public void aspect() {
        // 切入点方法
    }

    /**
     * 前置切点
     *
     * @param joinPoint 切入点
     */
    @Before("aspect()")
    public void doBefore(JoinPoint joinPoint) {
        LOGGER.info("-------------------------------doBefore begin------------------------------------");
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        RequestLimit limit = targetMethod.getAnnotation(RequestLimit.class);
        LOGGER.info("限流方式：【{}】", limit.type().getValue());
        RequestLimitService service = factory.build(limit.type());
        if (service == null) {
            LOGGER.info("【{}】无对应限流操作类型，直接放行", limit.type());
        } else {
            RequestLimitDTO dto = new RequestLimitDTO();
            dto.setLimit(limit);
            dto.setKey(signature.getName());
            if (service.checkRequestLimit(dto)) {
                throw new RuntimeException("【" + limit.type().getValue() + "】限流控制");
            }
        }
        LOGGER.info("-------------------------------doBefore end------------------------------------");
    }

    /**
     * 环绕切点
     *
     * @param proceedingJoinPoint 切入点
     * @return 方法执行返回
     */
    @Around("aspect()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            return e.getMessage();
        }
    }

    /**
     * 后置切入点
     *
     * @param joinPoint 切入点
     * @param obj       返回值
     */
    @AfterReturning(pointcut = "aspect()", returning = "obj")
    public void doAfterReturning(JoinPoint joinPoint, Object obj) {
//        LOGGER.info("-------------------------------doAfterReturning begin------------------------------------");
//        LOGGER.info("doAfterReturning......");
//        LOGGER.info("doAfterReturning - 方法名称：" + joinPoint.getSignature().getName());
//        LOGGER.info("doAfterReturning - 返回值：" + obj);
//        LOGGER.info("-------------------------------doAfterReturning end------------------------------------");
    }

}
