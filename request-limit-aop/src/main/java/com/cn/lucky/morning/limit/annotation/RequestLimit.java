package com.cn.lucky.morning.limit.annotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

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
    int type() default 1;

    int limitCount() default 100;
}
