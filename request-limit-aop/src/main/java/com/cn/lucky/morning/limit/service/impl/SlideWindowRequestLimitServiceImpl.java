package com.cn.lucky.morning.limit.service.impl;

import com.cn.lucky.morning.limit.annotation.RequestLimit;
import com.cn.lucky.morning.limit.common.RedisKeyConstant;
import com.cn.lucky.morning.limit.dto.RequestLimitDTO;
import com.cn.lucky.morning.limit.enmus.RequestLimitType;
import com.cn.lucky.morning.limit.service.RequestLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.UUID;

/**
 * <p>
 * 滑动窗口 限流
 * </p>
 *
 * @author wangchen
 * @since 2021/11/25
 */
@Service
public class SlideWindowRequestLimitServiceImpl implements RequestLimitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlideWindowRequestLimitServiceImpl.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean checkRequestLimit(RequestLimitDTO dto) {
        String key = RedisKeyConstant.RequestLimit.QPS_SLIDE_WINDOW + dto.getKey();
        RequestLimit limit = dto.getLimit();
        long current = System.currentTimeMillis();
        long duringTime = limit.unit().toMillis(limit.time());
        Long count = redisTemplate.opsForZSet().count(key, current - duringTime, current);

        LOGGER.info("限流配置：{} {} 内允许访问 {} 次", limit.time(), limit.unit(), limit.limitCount());
        LOGGER.info("访问时间【{}】", LocalTime.now().toString());
        // 检测是否到达限流值
        if (count != null && count >= limit.limitCount()) {
            String msg = "【" + key + "】限流控制，" + limit.time() + " " + limit.unit().name() + "内只允许访问 " + limit.limitCount() + " 次";
            LOGGER.info(msg);
            return true;
        } else {
            redisTemplate.opsForZSet().add(key, UUID.randomUUID().toString(), System.currentTimeMillis());
            LOGGER.info("未达到限流值，放行 {}/{}", count, limit.limitCount());
            return false;
        }
    }

    @Override
    public RequestLimitType getType() {
        return RequestLimitType.SLIDE_WINDOW;
    }
}
