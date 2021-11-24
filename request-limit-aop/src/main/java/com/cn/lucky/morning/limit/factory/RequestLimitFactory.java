package com.cn.lucky.morning.limit.factory;

import com.cn.lucky.morning.limit.enmus.RequestLimitType;
import com.cn.lucky.morning.limit.service.RequestLimitService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RequestLimitFactory
 * 限流工厂类
 *
 * @author wangchen
 * @group com.cn.lucky.morning.limit.factory
 * @date 2021/11/24 16:21
 */
@Component
public class RequestLimitFactory implements ApplicationContextAware {
    private static final Map<RequestLimitType, RequestLimitService> MAP = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.getBeansOfType(RequestLimitService.class).values().forEach(service -> {
            MAP.put(service.getType(), service);
        });
    }

    /**
     * 构建service
     *
     * @param type 限流类型
     * @return 操作类
     */
    public RequestLimitService build(RequestLimitType type) {
        return MAP.get(type);
    }
}
