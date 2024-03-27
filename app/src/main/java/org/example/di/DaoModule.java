/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
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
        log.trace("Providing User Dao");
        return DaoManager.createDao(connectionSource, User.class);
    }

    @SneakyThrows
    @Provides
    public Dao<Role, UUID> provideRoleDao(ConnectionSource connectionSource) {
        log.trace("Providing Role Dao");
        return DaoManager.createDao(connectionSource, Role.class);
    }

    @SneakyThrows
    @Provides
    public Dao<UserRole, UUID> provideUserRoleDao(ConnectionSource connectionSource) {
        log.trace("Providing UserRole Dao");
        return DaoManager.createDao(connectionSource, UserRole.class);
    }

    @SneakyThrows
    @Provides
    public Dao<Log, UUID> provideLogDao(ConnectionSource connectionSource) {
        log.trace("Providing Log Dao");
        return DaoManager.createDao(connectionSource, Log.class);
    }

    @SneakyThrows
    @Provides
    public Dao<SystemConfig, UUID> provideSystemConfigDao(ConnectionSource connectionSource) {
        log.trace("Providing SystemConfig Dao");
        return DaoManager.createDao(connectionSource, SystemConfig.class);
    }

    // nodeDao
    @SneakyThrows
    @Provides
    public Dao<Node, UUID> provideNodeDao(ConnectionSource connectionSource) {
        log.trace("Providing Node Dao");
        return DaoManager.createDao(connectionSource, Node.class);
    }

    // clientDao
    @SneakyThrows
    @Provides
    public Dao<Client, UUID> provideClientDao(ConnectionSource connectionSource) {
        log.trace("Providing Client Dao");
        return DaoManager.createDao(connectionSource, Client.class);
    }

    // applicationDao
    @SneakyThrows
    @Provides
    public Dao<Application, UUID> provideApplicationDao(ConnectionSource connectionSource) {
        log.trace("Providing Application Dao");
        return DaoManager.createDao(connectionSource, Application.class);
    }

    @SneakyThrows
    @Provides
    public Dao<Invoice, UUID> provideInvoiceDao(ConnectionSource connectionSource) {
        log.trace("Providing Application Dao");
        return DaoManager.createDao(connectionSource, Invoice.class);
    }

    @SneakyThrows
    @Provides
    public Dao<Order, UUID> provideOrderDao(ConnectionSource connectionSource) {
        log.trace("Providing Application Dao");
        return DaoManager.createDao(connectionSource, Order.class);
    }

    @SneakyThrows
    @Provides
    public Dao<Plan, UUID> providePlanDao(ConnectionSource connectionSource) {
        log.trace("Providing Application Dao");
        return DaoManager.createDao(connectionSource, Plan.class);
    }

    @SneakyThrows
    @Provides
    public Dao<Session, UUID> provideSessionDao(ConnectionSource connectionSource) {
        log.trace("Providing Application Dao");
        return DaoManager.createDao(connectionSource, Session.class);
    }
}
