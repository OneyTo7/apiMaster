package com.apimaster.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {

    /**
     * 设置缓存
     * @param key 键
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 设置带过期时间的缓存
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 删除缓存
     * @param key 键
     */
    void delete(String key);

    /**
     * 检查键是否存在
     * @param key 键
     * @return 是否存在
     */
    boolean exists(String key);

    /**
     * 设置键的过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否成功
     */
    boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 获取键的剩余过期时间
     * @param key 键
     * @param unit 时间单位
     * @return 剩余过期时间
     */
    long getExpire(String key, TimeUnit unit);
}
