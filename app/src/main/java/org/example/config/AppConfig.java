/* (C) 2023 */
package org.example.config;

import com.typesafe.config.Config;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class AppConfig {

    @Getter(AccessLevel.NONE)
    private final Config config;

    private final String version;
    private final String defaultLocale;
    private final boolean prettyPrint;

    private final int serverMinThreads;
    private final int serverMaxThreads;
    private final int serverIdleTimeoutSeconds;

    private final String listenAddress;
    private final int listenPort;

    private final String databaseUrl;
    private final String databaseUsername;
    private final String databasePassword;

    public AppConfig(@NotNull Config config) {
        this.config = config;

        version = config.getString("app.version");
        defaultLocale = config.getString("app.default-locale");
        prettyPrint = config.getBoolean("app.pretty-print");

        serverMinThreads = config.getInt("server.min-threads");
        serverMaxThreads = config.getInt("server.max-threads");
        serverIdleTimeoutSeconds = config.getInt("server.idle-timeout-seconds");

        listenAddress = config.getString("listen.address");
        listenPort = config.getInt("listen.port");

        databaseUrl = config.getString("database.url");
        databaseUsername = config.getString("database.username");
        databasePassword = config.getString("database.password");
    }
}
