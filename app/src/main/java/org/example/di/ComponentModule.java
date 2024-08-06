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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.*;
import org.example.entity.*;

@Slf4j
@Module
public class ComponentModule {

    @Provides
    @Singleton
    @Named("tables")
    public List<Class<?>> provideTables() {
        List<Class<?>> tables = List.of(
                User.class,
                Role.class,
                UserRole.class,
                Log.class,
                SystemConfig.class,
                Node.class,
                Client.class,
                Application.class,
                Invoice.class,
                Order.class,
                Plan.class,
                Session.class);
        log.info("registering {} tables", tables.size());
        return tables;
    }

    @Provides
    @Singleton
    @Named("controllers")
    public Map<Class<?>, Object> provideControllers(
            AdminController adminController,
            AuthController authController,
            CustomerController customerController,
            DashboardController dashboardController,
            ProviderController providerController,
            UserController userController) {
        Map<Class<?>, Object> controllers = new HashMap<>();
        controllers.put(AdminController.class, adminController);
        controllers.put(AuthController.class, authController);
        controllers.put(CustomerController.class, customerController);
        controllers.put(DashboardController.class, dashboardController);
        controllers.put(ProviderController.class, providerController);
        controllers.put(UserController.class, userController);

        log.info("registering {} controllers", controllers.size());
        return controllers;
    }
}
