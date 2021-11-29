package com.cn.lucky.morning.limit.controller;

import com.cn.lucky.morning.limit.annotation.RequestLimit;
import com.cn.lucky.morning.limit.enums.RequestLimitType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 测试限流控制器
 * </p>
 *
 * @author wangchen
 * @since 2021/11/23
 */
@RestController
@RequestMapping
public class LimitController {

    /**
     * 测试AOP拦截 固定窗口限流
     *
     * @return 返回结果
     */
    @RequestLimit(type = RequestLimitType.FIXED_WINDOW, limitCount = 2, time = 5)
    @GetMapping("/aop-fixed-window-test")
    public String aopFixedWindowTest() {
        return "固定窗口限流 - 接口返回";
    }

    /**
     * 测试AOP拦截 滑动窗口 限流
     *
     * @return 返回结果
     */
    @RequestLimit(type = RequestLimitType.SLIDE_WINDOW, limitCount = 2, time = 5)
    @GetMapping("/aop-slide-window-test")
    public String aopSlideWindowTest() {
        return "滑动窗口限流 - 接口返回";
    }

    /**
     * 测试AOP拦截 令牌桶 限流
     *
     * @return 返回结果
     */
    @RequestLimit(type = RequestLimitType.TOKEN, limitCount = 2, limitPeriodCount = 1, period = 1000)
    @GetMapping("/aop-token-test")
    public String aopTokenTest() {
        return "【" + RequestLimitType.TOKEN.getValue() + "】限流 - 接口返回";
    }

    /**
     * 测试AOP拦截 漏桶 限流
     *
     * @return 返回结果
     */
    @RequestLimit(type = RequestLimitType.LEAKY_BUCKET, limitCount = 2, limitPeriodCount = 1, period = 1000)
    @GetMapping("/aop-leaky-bucket-test")
    public String aopLeakyBucketTest() {
        return "【" + RequestLimitType.LEAKY_BUCKET.getValue() + "】限流 - 接口返回";
    }
}
