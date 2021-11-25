package com.cn.lucky.morning.limit.service;

import com.cn.lucky.morning.limit.dto.RequestLimitDTO;
import com.cn.lucky.morning.limit.enmus.RequestLimitType;

/**
 * RequestLimitService
 * 限流检测 接口类
 *
 * @author wangchen
 * @group com.cn.lucky.morning.limit.service
 * @date 2021/11/24 16:06
 */
public interface RequestLimitService {
    /**
     * 检测是否限流
     *
     * @param dto 限流参数
     * @return 是否被限流 true: 被限流  false: 未限流，放行
     */
    boolean checkRequestLimit(RequestLimitDTO dto);

    /**
     * 获取当前限流类型
     *
     * @return 限流类型
     */
    RequestLimitType getType();
}
