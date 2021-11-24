package com.cn.lucky.morning.limit.common;

/**
 * RedisKeyConstant
 * redis相关常量
 *
 * @author wangchen
 * @group com.cn.lucky.morning.limit.common
 * @date 2021/11/24 15:07
 */
public interface RedisKeyConstant {

    /**
     * 限流相关
     */
    interface RequestLimit {

        /**
         * 固定窗口键名
         */
        String QPS_FIXED_WINDOW = "request:limit:qps:fixedWindow:";

        /**
         * 固定窗口键名
         */
        String QPS_SLIDE_WINDOW = "request:limit:qps:slideWindow:";

        /**
         * 固定窗口键名
         */
        String QPS_TOKEN = "request:limit:qps:token:";

        /**
         * 固定窗口键名
         */
        String QPS_LEAKY_BUCKET = "request:limit:qps:leakyBucket:";
    }
}
