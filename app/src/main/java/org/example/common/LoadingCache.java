/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.common;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

@Slf4j
public class LoadingCache<K, V> {

    private final Config config;
    private final RMapCache<K, V> cache;

    public LoadingCache(@NonNull RedissonClient redissonClient, @Nullable Config config) {
        this.config = config != null ? config : new Config();
        this.cache = redissonClient.getMapCache(this.config.name);
        this.cache.setMaxSize(this.config.maxSize);
    }

    @Data
    public static class Config {
        private String name =
                "cache:" + StringUtils.lowerCase(UUID.randomUUID().toString().replace("-", ""));
        private long expirationSeconds = 3600; // 1 hour
        private int maxSize = 10000;
    }

    public interface CacheLoader<K, V> {
        V load(K key) throws SQLException;
    }

    public V get(K key, @NonNull CacheLoader<K, V> loader) throws SQLException {
        V value = cache.get(key);
        if (value == null) {
            value = loader.load(key);
            cache.fastPut(key, value, config.expirationSeconds, TimeUnit.SECONDS);
        }
        log.trace("get key: {}, value: {} from cache", key, value);
        return value;
    }

    public void remove(K key) {
        cache.fastRemove(key);
    }
}
