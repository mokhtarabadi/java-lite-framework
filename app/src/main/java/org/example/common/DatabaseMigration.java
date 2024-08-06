/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.common;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.entity.*;
import org.flywaydb.core.Flyway;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DatabaseMigration {

    @NonNull private ConnectionSource connectionSource;

    @NonNull private DataSource dataSource;

    @Named("tables")
    @NonNull private List<Class<?>> tables;

    public void migrate() throws SQLException {
        // create tables
        for (Class<?> table : tables) {
            TableUtils.createTableIfNotExists(connectionSource, table);
        }

        // configure flyway
        Flyway flyway = Flyway.configure()
                .validateOnMigrate(true)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .dataSource(dataSource)
                .table("migrations")
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
    }

    public void release() throws SQLException {
        connectionSource.closeQuietly();
        dataSource.getConnection().close();
    }
}
