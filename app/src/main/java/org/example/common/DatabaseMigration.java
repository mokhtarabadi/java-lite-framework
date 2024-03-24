/* (C) 2023 */
package org.example.common;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.entity.*;
import org.flywaydb.core.Flyway;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DatabaseMigration {

    @NonNull private ConnectionSource connectionSource;

    @NonNull private HikariDataSource hikariDataSource;

    public void migrate() throws SQLException {
        // create tables
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Role.class);
        TableUtils.createTableIfNotExists(connectionSource, UserRole.class);
        TableUtils.createTableIfNotExists(connectionSource, Log.class);
        TableUtils.createTableIfNotExists(connectionSource, SystemConfig.class);

        // configure flyway
        Flyway flyway = Flyway.configure()
                .validateOnMigrate(true)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .dataSource(hikariDataSource)
                .table("migrations")
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
    }

    public void release() {
        connectionSource.closeQuietly();
        hikariDataSource.close();
    }
}
