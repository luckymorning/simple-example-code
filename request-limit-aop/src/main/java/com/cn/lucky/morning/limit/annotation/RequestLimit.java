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

    /**
     * 漏出或者生成令牌时间间隔，单位 毫秒  (当type为TOKEN、LEAKY_BUCKET时生效)
     */
    long period() default 1000;

    /**
     * 每次生成令牌数或者漏出水滴数  (当type为TOKEN、LEAKY_BUCKET时生效)
     */
    int limitPeriodCount() default 10;
}
