package com.cn.lucky.morning.limit.service.impl;

import com.cn.lucky.morning.limit.annotation.RequestLimit;
import com.cn.lucky.morning.limit.dto.RequestLimitDTO;
import com.cn.lucky.morning.limit.enmus.RequestLimitType;
import com.cn.lucky.morning.limit.service.RequestLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;

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
    public void checkRequestLimit(RequestLimitDTO dto) {
        RequestLimit limit = dto.getLimit();
        RedisAtomicInteger atomicCount = new RedisAtomicInteger(dto.getKey(), factory);
        int count = atomicCount.getAndIncrement();
        if (count == 0) {
            atomicCount.expire(limit.time(), limit.unit());
        }
        // 检测是否到达限流值
        if (count >= limit.limitCount()) {
            String msg = "【" + dto.getKey() + "】限流控制，" + limit.time() + " " + limit.unit().name() + "内只允许访问 " + limit.limitCount() + " 次";
            LOGGER.info(msg);
            throw new RuntimeException("限流控制");
        } else {
            LOGGER.info("未达到限流值，放行 {}/{}", count, limit.limitCount());
        }
    }

    @Override
    public RequestLimitType getType() {
        return RequestLimitType.FIXED_WINDOW;
    }
}
