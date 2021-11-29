package com.cn.lucky.morning.limit.service.impl;

import com.cn.lucky.morning.limit.annotation.RequestLimit;
import com.cn.lucky.morning.limit.common.RedisKeyConstant;
import com.cn.lucky.morning.limit.dto.RequestLimitDTO;
import com.cn.lucky.morning.limit.enums.RequestLimitType;
import com.cn.lucky.morning.limit.service.RequestLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * TokenRequestLimitServiceImpl
 * 令牌桶 限流
 *
 * @author wangchen
 * @group com.cn.lucky.morning.limit.service.impl
 * @date 2021/11/26 12:15
 */
@Service
public class TokenRequestLimitServiceImpl implements RequestLimitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenRequestLimitServiceImpl.class);

    @javax.annotation.Resource(name = "tokenPushThreadPoolScheduler")
    private ThreadPoolTaskScheduler scheduler;

    @Value("${request-limit.scan-package:}")
    private String scanPackage;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean checkRequestLimit(RequestLimitDTO dto) {
        Object pop = redisTemplate.opsForList().rightPop(RedisKeyConstant.RequestLimit.QPS_TOKEN + dto.getKey());
        RequestLimit limit = dto.getLimit();
        LOGGER.info("限流配置：每 {} 毫秒 生成 {} 个令牌，最大令牌数：{}", limit.period(), limit.limitPeriodCount(), limit.limitCount());
        return pop == null;
    }

    @Override
    public RequestLimitType getType() {
        return RequestLimitType.TOKEN;
    }

    /**
     * 定速生成令牌
     */
    @PostConstruct
    public void pushToken() {
        List<RequestLimitDTO> list = this.getTokenLimitList(resourcePatternResolver, RequestLimitType.TOKEN, scanPackage);
        if (list.isEmpty()) {
            LOGGER.info("未扫描到使用 令牌限流 注解的方法，结束生成令牌线程");
            return;
        }

        list.forEach(limit -> scheduler.scheduleAtFixedRate(() -> {
            String key = RedisKeyConstant.RequestLimit.QPS_TOKEN + limit.getKey();
            Long size = redisTemplate.opsForList().size(key);
            if (size == null) {
                size = 0L;
            }
            if (size.intValue() >= limit.getLimit().limitCount()) {
                LOGGER.info("【{}】令牌数量已达最大值【{}】，丢弃新生成令牌", key, size);
                return;
            }
            int addSize = size == 0 ? limit.getLimit().limitPeriodCount() : Math.min(limit.getLimit().limitPeriodCount(), Math.abs(size.intValue() - limit.getLimit().limitPeriodCount()));
            List<String> addList = new ArrayList<>(addSize);
            for (int index = 0; index < addSize; index++) {
                addList.add(UUID.randomUUID().toString());
            }
            redisTemplate.opsForList().leftPushAll(key, addList);
            LOGGER.info("【{}】生成令牌丢入令牌桶", key);
        }, limit.getLimit().period()));
    }
}
