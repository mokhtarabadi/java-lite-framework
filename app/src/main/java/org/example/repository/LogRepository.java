/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.repository;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Log;
import org.example.mapper.LogMapper;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class LogRepository extends AbstractCrudRepository<Log> {

    public long countByTypes(Log.Type[] types) throws SQLException {
        QueryBuilder<Log, UUID> builder = getDao().queryBuilder();
        builder.where().in("type", (Object[]) types);
        return builder.countOf();
    }
}
