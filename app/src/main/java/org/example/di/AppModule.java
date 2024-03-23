/* (C) 2023 */
package org.example.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.jdbc.db.MariaDbDatabaseType;
import com.j256.ormlite.support.ConnectionSource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariDataSource;
import dagger.Module;
import dagger.Provides;
import de.neuland.pug4j.Pug4J;
import java.io.File;
import java.util.*;
import javax.inject.Singleton;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.example.common.ClosureFilter;
import org.example.common.CustomValidator;
import org.example.common.Localization;
import org.example.common.PugTemplateEngine;
import org.example.config.AppConfig;
import org.jetbrains.annotations.NotNull;
import spark.TemplateEngine;

@Module
public class AppModule {

    @Provides
    @Singleton
    public Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.disableHtmlEscaping();
        // gsonBuilder.serializeNulls();
        gsonBuilder.setLenient();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");

        // custom adapters

        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    public Config provideConfig() {
        Config config = ConfigFactory.load();

        File file = new File(System.getProperty("user.dir") + File.separator + "app.conf");
        if (file.exists()) {
            return ConfigFactory.parseFile(file).withFallback(config);
        }

        return config;
    }

    @Provides
    public AppConfig provideAppConfig(Config config) {
        return new AppConfig(config);
    }

    @Provides
    @Singleton
    public HikariDataSource provideHikariDataSource(@NotNull AppConfig appConfig) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(appConfig.getDatabaseUrl());
        ds.setUsername(appConfig.getDatabaseUsername());
        ds.setPassword(appConfig.getDatabasePassword());
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        ds.setMaximumPoolSize(100);
        ds.setConnectionTestQuery("SELECT 1 FROM DUAL");
        ds.setMaxLifetime(60000L);
        ds.setMinimumIdle(5);
        ds.setConnectionTimeout(30000L);
        return ds;
    }

    @SneakyThrows
    @Provides
    @Singleton
    public ConnectionSource provideConnectionSource(@NotNull HikariDataSource hikariDataSource) {
        MariaDbDatabaseType mariaDbDatabaseType = new MariaDbDatabaseType();
        return new DataSourceConnectionSource(hikariDataSource, mariaDbDatabaseType);
    }

    @Provides
    @Singleton
    public Localization provideLocalization(AppConfig appConfig) {
        return new Localization(appConfig);
    }

    @Provides
    @Singleton
    public CustomValidator provideValidator(@NonNull AppConfig appConfig, @NotNull Localization localization) {
        CustomValidator customValidator = new CustomValidator(localization, appConfig);
        customValidator.init();
        return customValidator;
    }

    @Provides
    @Singleton
    public TemplateEngine provideTemplateEngine(
            @NotNull Localization localization, @NotNull AppConfig appConfig, Gson gson) {
        PugTemplateEngine templateEngine = new PugTemplateEngine("/templates");
        templateEngine.getConfiguration().setPrettyPrint(appConfig.isPrettyPrint());
        templateEngine.getConfiguration().setMode(Pug4J.Mode.HTML);
        templateEngine.getConfiguration().setCaching(true);

        Map<String, Map<String, Object>> bundles = new HashMap<>();
        for (String supportedLocale : localization.getSupportedLocales()) {
            Map<String, Object> bundle = new HashMap<>();
            localization
                    .getBundle(supportedLocale)
                    .keySet()
                    .forEach(key -> bundle.put(
                            key, localization.getBundle(supportedLocale).getObject(key)));

            bundles.put(supportedLocale, bundle);
        }

        Map<String, Object> sharedVariables = new HashMap<>();
        sharedVariables.put("bundles", bundles);
        sharedVariables.put("gson", gson);

        templateEngine.getConfiguration().setSharedVariables(sharedVariables);

        templateEngine.getConfiguration().setFilter("closure", new ClosureFilter(appConfig.isPrettyPrint()));

        return templateEngine;
    }
}
