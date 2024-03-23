/* (C) 2023 */
package org.example.di;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import dagger.Module;
import dagger.Provides;
import java.util.UUID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.*;

@Module
@Slf4j
public class DaoModule {

    @SneakyThrows
    @Provides
    public Dao<User, UUID> provideUserDao(ConnectionSource connectionSource) {
        return DaoManager.createDao(connectionSource, User.class);
    }

    @SneakyThrows
    @Provides
    public Dao<Role, UUID> provideRoleDao(ConnectionSource connectionSource) {
        return DaoManager.createDao(connectionSource, Role.class);
    }

    @SneakyThrows
    @Provides
    public Dao<UserRole, UUID> provideUserRoleDao(ConnectionSource connectionSource) {
        return DaoManager.createDao(connectionSource, UserRole.class);
    }

    @SneakyThrows
    @Provides
    public Dao<Log, UUID> provideLogDao(ConnectionSource connectionSource) {
        return DaoManager.createDao(connectionSource, Log.class);
    }

    @SneakyThrows
    @Provides
    public Dao<SystemConfig, UUID> provideSystemConfigDao(ConnectionSource connectionSource) {
        return DaoManager.createDao(connectionSource, SystemConfig.class);
    }
}
