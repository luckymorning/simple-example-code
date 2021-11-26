package com.cn.lucky.morning.limit.enmus;

/**
 * RequestLimitType
 * 限流类型
 *
 * @author wangchen
 * @group com.cn.lucky.morning.limit.enmus
 * @date 2021/11/24 16:46
 */
public enum RequestLimitType {
    /**
     * 固定窗口
     */
    FIXED_WINDOW("固定窗口"),
    /**
     * 滑动窗口
     */
    SLIDE_WINDOW("滑动窗口"),
    /**
     * 令牌算法
     */
    TOKEN("令牌算法"),
    /**
     * 漏桶算法
     */
    LEAKY_BUCKET("漏桶算法"),
    ;
    private final String value;

    RequestLimitType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
