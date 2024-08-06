/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example;

import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.swing.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.DatabaseMigration;
import org.example.common.Localization;
import org.example.common.Router;
import org.example.config.AppConfig;
import org.example.controller.*;
import org.example.di.AppComponent;
import org.example.di.DaggerAppComponent;
import org.example.util.Utility;
import spark.Spark;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class App {

    @NonNull private AppConfig appConfig;

    @NonNull private DatabaseMigration databaseMigration;

    @NonNull private Router router;

    @NonNull private Localization localization;

    @NonNull private AdminController adminController;

    @NonNull private AuthController authController;

    @NonNull private CustomerController customerController;

    @NonNull private DashboardController dashboardController;

    @NonNull private ProviderController providerController;

    @NonNull private UserController userController;

    private void startServer() {
        // migrate database
        try {
            databaseMigration.migrate();
        } catch (SQLException e) {
            log.error("failed to migrate database", e);
        }

        Spark.threadPool(
                appConfig.getServerMaxThreads(),
                appConfig.getServerMinThreads(),
                appConfig.getServerIdleTimeoutSeconds());

        // register websockets services here

        // config server
        Spark.ipAddress(appConfig.getListenAddress());
        Spark.port(appConfig.getListenPort());

        // handle static files
        Spark.staticFileLocation("/static");

        Spark.staticFiles.expireTime(TimeUnit.MINUTES.toSeconds(30));

        Spark.initExceptionHandler(e -> {
            log.error("failed to start spark server", e);

            // Spark.stop();
            // Spark.awaitStop();
            System.exit(100);
        });
        Spark.init();
        Spark.awaitInitialization();

        // register routing
        try {
            router.registerRoutes();
        } catch (Exception e) {
            log.error("failed to register routes", e);
        }

        log.info(
                "{} version {} started at http://{}:{}",
                localization.getString(appConfig.getDefaultLocale(), "app.name"),
                appConfig.getVersion(),
                appConfig.getListenAddress(),
                appConfig.getListenPort());
    }

    private void stopServer() throws SQLException {
        log.info("stopping app");
        databaseMigration.release();
        Spark.stop();
        Spark.awaitStop();
    }

    public static void main(String[] args) {
        // create dagger app
        AppComponent appComponent = DaggerAppComponent.create();

        // inject utility class
        appComponent.inject(Utility.getInstance());

        // configure gui
        App app = appComponent.getApp();
        app.startServer();
    }
}
