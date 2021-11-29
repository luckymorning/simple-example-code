package com.cn.lucky.morning.limit.service.impl;

import com.cn.lucky.morning.limit.annotation.RequestLimit;
import com.cn.lucky.morning.limit.common.RedisKeyConstant;
import com.cn.lucky.morning.limit.dto.RequestLimitDTO;
import com.cn.lucky.morning.limit.enums.RequestLimitType;
import com.cn.lucky.morning.limit.service.RequestLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

/**
 * FixedWindowRequestLimitServiceImpl
 * 固定窗口 限流
 *
 * @author wangchen
 * @group com.cn.lucky.morning.limit.service.impl
 * @date 2021/11/24 16:17
 */
@Service
public class FixedWindowRequestLimitServiceImpl implements RequestLimitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FixedWindowRequestLimitServiceImpl.class);

    @Autowired
    private RedisConnectionFactory factory;

    @Override
    public boolean checkRequestLimit(RequestLimitDTO dto) {
        String key = RedisKeyConstant.RequestLimit.QPS_FIXED_WINDOW + dto.getKey();
        RequestLimit limit = dto.getLimit();
        RedisAtomicInteger atomicCount = new RedisAtomicInteger(key, factory);
        int count = atomicCount.getAndIncrement();
        if (count == 0) {
            atomicCount.expire(limit.time(), limit.unit());
        }
        LOGGER.info("限流配置：{} {} 内允许访问 {} 次", limit.time(), limit.unit(), limit.limitCount());
        LOGGER.info("访问时间【{}】", LocalTime.now());
        // 检测是否到达限流值
        if (count >= limit.limitCount()) {
            String msg = "【" + key + "】限流控制，" + limit.time() + " " + limit.unit().name() + "内只允许访问 " + limit.limitCount() + " 次";
            LOGGER.info(msg);
            return true;
        } else {
            LOGGER.info("未达到限流值，放行 {}/{}", count, limit.limitCount());
            return false;
        }
    }

    @Override
    public RequestLimitType getType() {
        return RequestLimitType.FIXED_WINDOW;
    }
}
