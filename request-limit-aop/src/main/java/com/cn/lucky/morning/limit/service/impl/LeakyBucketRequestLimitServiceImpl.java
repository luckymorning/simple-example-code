package com.cn.lucky.morning.limit.service.impl;

import com.cn.lucky.morning.limit.common.RedisKeyConstant;
import com.cn.lucky.morning.limit.dto.RequestLimitDTO;
import com.cn.lucky.morning.limit.enmus.RequestLimitType;
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
import java.util.*;

/**
 * LeakyBucketRequestLimitServiceImpl
 * 漏桶算法 限流
 *
 * @author wangchen
 * @group com.cn.lucky.morning.limit.service.impl
 * @date 2021/11/26 17:49
 */
@Service
public class LeakyBucketRequestLimitServiceImpl implements RequestLimitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeakyBucketRequestLimitServiceImpl.class);

    @javax.annotation.Resource(name = "leakyBucketPopThreadPoolScheduler")
    private ThreadPoolTaskScheduler scheduler;

    @Value("${request-limit.leaky-bucket.period:10000}")
    private long period;

    @Value("${request-limit.leaky-bucket.count:10}")
    private int count;

    @Value("${request-limit.scan-package:}")
    private String scanPackage;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean checkRequestLimit(RequestLimitDTO dto) {
        Long size = redisTemplate.opsForList().size(RedisKeyConstant.RequestLimit.QPS_LEAKY_BUCKET);
        return size != null && size >= count;
    }

    @PostConstruct
    public void popToken() {
        List<RequestLimitDTO> list = this.getTokenLimitList(resourcePatternResolver, RequestLimitType.LEAKY_BUCKET, scanPackage);
        if (list.isEmpty()) {
            LOGGER.debug("未扫描到使用 漏桶限流 注解的方法，结束生成令牌线程");
            return;
        }
        redisTemplate.delete(RedisKeyConstant.RequestLimit.QPS_LEAKY_BUCKET);
        scheduler.scheduleAtFixedRate(() -> {
            for (int index = 0; index < count; index++) {
                redisTemplate.opsForList().trim(RedisKeyConstant.RequestLimit.QPS_LEAKY_BUCKET, count, -1);
                LOGGER.debug("漏出 {} 个水滴", count);
            }
        }, period);
    }

    @Override
    public RequestLimitType getType() {
        return RequestLimitType.LEAKY_BUCKET;
    }
}
