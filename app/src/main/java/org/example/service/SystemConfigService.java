/* (C) 2024 */
package org.example.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.contract.SystemConfigContract;
import org.example.entity.SystemConfig;
import org.example.repository.SystemConfigRepository;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Singleton
public class SystemConfigService implements SystemConfigContract {

    @NonNull private SystemConfigRepository systemConfigRepository;

    @Override
    public String getConfigByKey(String key) throws SQLException {
        // TODO: 3/23/24 add caching 
        Optional<SystemConfig> systemConfig = systemConfigRepository.getByKey(key);
        return systemConfig.map(SystemConfig::getValue).orElse(null);
    }

    @Override
    public void writeConfig(String key, String value) throws SQLException {
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
