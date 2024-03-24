/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.LoadingCache;
import org.example.contract.SystemConfigContract;
import org.example.entity.SystemConfig;
import org.example.repository.SystemConfigRepository;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Singleton
public class SystemConfigService implements SystemConfigContract {

    @NonNull private SystemConfigRepository systemConfigRepository;

    @NonNull private LoadingCache<String, String> simpleCache;

    private final LoadingCache.CacheLoader<String, String> simpleCacheLoader = new LoadingCache.CacheLoader<>() {
        @Override
        public String load(String key) throws SQLException {
            Optional<SystemConfig> systemConfig = systemConfigRepository.getByKey(key);
            return systemConfig.map(SystemConfig::getValue).orElse(null);
        }
    };

    @Override
    public String getConfigByKey(String key) throws SQLException {
        return simpleCache.get(key, simpleCacheLoader);
    }

    @Override
    public void writeConfig(String key, String value) throws SQLException {
        // remove cache
        simpleCache.remove(key);

        SystemConfig systemConfig = systemConfigRepository.getByKey(key).orElse(null);
        if (systemConfig != null) {
            // updating old config
            systemConfig.setValue(value);
            systemConfig.setUpdatedAt(new Date());
            systemConfigRepository.update(systemConfig);
            return;
        }

        systemConfig = new SystemConfig(key, value);
        systemConfigRepository.create(systemConfig);
    }
}
