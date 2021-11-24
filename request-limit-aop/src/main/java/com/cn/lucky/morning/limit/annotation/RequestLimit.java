package com.cn.lucky.morning.limit.annotation;

import com.cn.lucky.morning.limit.enmus.RequestLimitType;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 限流注解
 * </p>
 *
 * @author wangchen
 * @since 2021/11/23
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface RequestLimit {

    /**
     * 限流类型 1：固定窗口 2：滑动窗口  3：令牌算法 4：漏桶算法
     */
    RequestLimitType type() default RequestLimitType.FIXED_WINDOW;

    /**
     * 限流访问数
     */
    int limitCount() default 100;

    /**
     * 限流时间段
     */
    long time() default 60;

    /**
     * 限流时间段 时间单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;
}
