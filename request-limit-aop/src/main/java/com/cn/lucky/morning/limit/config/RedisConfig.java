package com.cn.lucky.morning.limit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * RedisConfig
 * redis配置
 *
 * @author wangchen
 * @group com.cn.lucky.morning.limit.config
 * @date 2021/11/24 15:31
 */
@Configuration
public class RedisConfig {
    @Autowired
    private RedisConnectionFactory factory;


    /**
     * 初始化RedisTemplate
     *
     * @return redisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }
}
