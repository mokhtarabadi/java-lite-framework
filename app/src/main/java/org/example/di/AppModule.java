/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
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
import javax.sql.DataSource;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.example.common.ClosureFilter;
import org.example.common.CustomValidator;
import org.example.common.Localization;
import org.example.common.PugTemplateEngine;
import org.example.config.AppConfig;
import org.jetbrains.annotations.NotNull;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.SingleServerConfig;
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
    public DataSource provideDataSource(@NotNull AppConfig appConfig) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(appConfig.getDatabaseUrl());
        ds.setUsername(appConfig.getDatabaseUsername());
        ds.setPassword(appConfig.getDatabasePassword());
        ds.setMaximumPoolSize(100);
        ds.setConnectionTestQuery("SELECT 1 FROM DUAL");
        return ds;
    }

    @SneakyThrows
    @Provides
    @Singleton
    public ConnectionSource provideConnectionSource(@NotNull DataSource dataSource, @NonNull AppConfig appConfig) {
        return new DataSourceConnectionSource(dataSource, appConfig.getDatabaseUrl());
    }

    @Provides
    @Singleton
    public RedissonClient provideRedissonClient(@NotNull AppConfig appConfig) {
        org.redisson.config.Config config = new org.redisson.config.Config();
        // config.setCodec(new org.redisson.codec.JsonJacksonCodec());
        SingleServerConfig singleServerConfig = config.useSingleServer().setAddress(appConfig.getRedisUrl());
        if (StringUtils.isNotBlank(appConfig.getRedisPassword())) {
            singleServerConfig.setPassword(appConfig.getRedisPassword());
        }
        return Redisson.create(config);
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
    public TemplateEngine provideTemplateEngine(@NotNull Localization localization, @NotNull AppConfig appConfig) {
        PugTemplateEngine templateEngine = new PugTemplateEngine("/templates");
        templateEngine.getConfiguration().setPrettyPrint(appConfig.isPrettyPrint());
        templateEngine.getConfiguration().setMode(Pug4J.Mode.HTML);
        templateEngine.getConfiguration().setCaching(true);

        List<Pair<String, String>> supportedLanguages = new ArrayList<>();
        Map<String, Map<String, Object>> bundles = new HashMap<>();
        for (String supportedLocale : localization.getSupportedLocales()) {
            Map<String, Object> bundle = new HashMap<>();
            localization
                    .getBundle(supportedLocale)
                    .keySet()
                    .forEach(key -> bundle.put(
                            key, localization.getBundle(supportedLocale).getObject(key)));

            bundles.put(supportedLocale, bundle);

            supportedLanguages.add(Pair.of(supportedLocale, localization.getString(supportedLocale, "language.name")));
        }

        Map<String, Object> sharedVariables = new HashMap<>();
        sharedVariables.put("bundles", bundles);
        sharedVariables.put("gson", new Gson());
        sharedVariables.put("defaultLocale", appConfig.getDefaultLocale());
        sharedVariables.put("supportedLanguages", supportedLanguages);

        templateEngine.getConfiguration().setSharedVariables(sharedVariables);

        templateEngine.getConfiguration().setFilter("closure", new ClosureFilter(appConfig.isPrettyPrint()));

        return templateEngine;
    }
}
