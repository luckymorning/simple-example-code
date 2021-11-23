package com.cn.lucky.morning.limit.controller;

import com.cn.lucky.morning.limit.annotation.RequestLimit;
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
     * 测试AOP拦截
     *
     * @return 返回结果
     */
    @RequestLimit(type = 1, limitCount = 1)
    @GetMapping("/aop-test")
    public String aopTest() {
        return "接口返回";
    }
}
