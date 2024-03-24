/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.di;

import dagger.Module;
import dagger.Provides;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import lombok.NonNull;
import org.example.common.LoadingCache;
import org.example.entity.User;
import org.redisson.api.RedissonClient;

@Module
public class CacheModule {

    @Provides
    @Singleton
    public LoadingCache<UUID, User> provideUserCache(@NonNull RedissonClient redissonClient) {
        LoadingCache.Config config = new LoadingCache.Config();
        config.setName("users");
        config.setExpirationSeconds(TimeUnit.MINUTES.toSeconds(10));
        return new LoadingCache<>(redissonClient, config);
    }

    @Provides
    @Singleton
    public LoadingCache<String, String> provideSimpleCache(@NonNull RedissonClient redissonClient) {
        LoadingCache.Config config = new LoadingCache.Config();
        config.setName("simple_cache");
        return new LoadingCache<>(redissonClient, config);
    }
}
