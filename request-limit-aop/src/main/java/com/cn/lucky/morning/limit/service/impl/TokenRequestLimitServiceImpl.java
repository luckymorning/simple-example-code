package com.cn.lucky.morning.limit.service.impl;

import com.cn.lucky.morning.limit.annotation.RequestLimit;
import com.cn.lucky.morning.limit.common.RedisKeyConstant;
import com.cn.lucky.morning.limit.dto.RequestLimitDTO;
import com.cn.lucky.morning.limit.enmus.RequestLimitType;
import com.cn.lucky.morning.limit.service.RequestLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

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

    @Value("${request-limit.token.max-count:100}")
    private int maxCount;

    @Value("${request-limit.token.period:10000}")
    private long period;

    @Value("${request-limit.token.count:10}")
    private int count;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean checkRequestLimit(RequestLimitDTO dto) {
        Object pop = redisTemplate.opsForList().rightPop(RedisKeyConstant.RequestLimit.QPS_TOKEN);
        LOGGER.info("限流配置：每 {} 毫秒 生成 {} 个令牌，最大令牌数：{}", period,count,maxCount);
        return pop == null;
    }

    @Override
    public RequestLimitType getType() {
        return RequestLimitType.TOKEN;
    }

    @PostConstruct
    public void pushToken() {
        List<RequestLimitDTO> list = this.getTokenLimitList();
        if (list.isEmpty()) {
            LOGGER.info("未扫描到使用 令牌限流 注解的方法，结束生成令牌线程");
            return;
        }
        redisTemplate.delete(RedisKeyConstant.RequestLimit.QPS_TOKEN);
        scheduler.scheduleAtFixedRate(() -> {
            for (int index = 0; index < count; index++) {
                Long size = redisTemplate.opsForList().size(RedisKeyConstant.RequestLimit.QPS_TOKEN);
                if (size != null && size >= maxCount) {
                    LOGGER.info("令牌数量已达最大值【{}】，丢弃新生成令牌", size);
                    return;
                }
                redisTemplate.opsForList().leftPush(RedisKeyConstant.RequestLimit.QPS_TOKEN, UUID.randomUUID().toString());
                LOGGER.info("生成令牌丢入令牌桶");
            }
        }, period);
    }

    private List<RequestLimitDTO> getTokenLimitList() {
        try {
            List<RequestLimitDTO> list = new ArrayList<>();

            Resource[] resources = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "com/cn/lucky/morning/limit/controller/**/*.class");

            MetadataReaderFactory metaReader = new CachingMetadataReaderFactory();
            for (Resource resource : resources) {
                MetadataReader reader = metaReader.getMetadataReader(resource);
                AnnotationMetadata annotationMetadata = reader.getAnnotationMetadata();

                Set<MethodMetadata> annotatedMethods = annotationMetadata.getAnnotatedMethods(RequestLimit.class.getCanonicalName());
                annotatedMethods.forEach(methodMetadata -> {
                    RequestLimit limit = methodMetadata.getAnnotations().get(RequestLimit.class).synthesize();
                    if (!RequestLimitType.TOKEN.equals(limit.type())) {
                        return;
                    }
                    RequestLimitDTO dto = new RequestLimitDTO();
                    dto.setKey(methodMetadata.getMethodName());
                    dto.setLimit(limit);
                    list.add(dto);
                });
            }
            return list;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
