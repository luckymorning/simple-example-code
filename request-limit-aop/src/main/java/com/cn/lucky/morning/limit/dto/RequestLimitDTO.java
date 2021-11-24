package com.cn.lucky.morning.limit.dto;

import com.cn.lucky.morning.limit.annotation.RequestLimit;

import java.io.Serializable;

/**
 * RequestLimitDTO
 * 限流参数DTO
 *
 * @author wangchen
 * @group com.cn.lucky.morning.limit.dto
 * @date 2021/11/24 17:10
 */
public class RequestLimitDTO implements Serializable {

    /**
     * 限流配置
     */
    private RequestLimit limit;

    /**
     * 拦截建
     */
    private String key;

    public RequestLimit getLimit() {
        return limit;
    }

    public void setLimit(RequestLimit limit) {
        this.limit = limit;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "RequestLimitDTO{" +
                "limit=" + limit +
                ", key='" + key + '\'' +
                '}';
    }
}
