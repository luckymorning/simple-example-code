package com.cn.lucky.morning.limit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * ThreadPoolConfig
 * 线程池配置
 *
 * @author wangchen
 * @group com.cn.lucky.morning.limit.config
 * @date 2021/11/26 14:13
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 生成令牌线程池
     *
     * @return 线程池实例化对象
     */
    @Bean(name = "tokenPushThreadPoolScheduler")
    public ThreadPoolTaskScheduler tokenPushThreadConfig() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("生成令牌桶线程");
        return scheduler;
    }

    /**
     * 漏桶滴水线程池
     *
     * @return 线程池实例化对象
     */
    @Bean(name = "leakyBucketPopThreadPoolScheduler")
    public ThreadPoolTaskScheduler leakyBucketPopThreadConfig() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("漏桶滴水线程");
        return scheduler;
    }
}
