/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.*;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.example.dto.DataTableRequestDTO;
import org.jetbrains.annotations.NotNull;

@Slf4j
public abstract class AbstractCrudRepository<T> {

    @Inject
    @Getter(AccessLevel.PACKAGE)
    protected Dao<T, UUID> dao;

    // Create
    public UUID create(T entity) throws SQLException {
        setCreatedAt(entity);
        UUID id = UUID.randomUUID();
        setId(entity, id);
        dao.create(entity);
        return id;
    }

    // Read
    public T read(UUID id) throws SQLException {
        return dao.queryForId(id);
    }

    // Update
    public int update(T entity) throws SQLException {
        return dao.update(entity);
    }

    // Delete
    public int delete(T entity) throws SQLException {
        return dao.delete(entity);
    }

    // Count
    public long count() throws SQLException {
        return dao.countOf();
    }

    // All
    public List<T> all() throws SQLException {
        return dao.queryForAll();
    }

    // Batch
    public <V> V batch(Callable<V> callable) throws Exception {
        return dao.callBatchTasks(callable);
    }

    // Delete by ID
    public int deleteById(UUID uuid) throws SQLException {
        DeleteBuilder<T, UUID> builder = dao.deleteBuilder();
        builder.where().eq("id", uuid);
        return builder.delete();
    }

    // DataTables
    @SafeVarargs
    public final List<T> queryForDataTable(
            long start,
            long length,
            List<DataTableRequestDTO.Column> columns,
            String search,
            int columnIndex,
            @NotNull String orderDir,
            Pair<String, Pair<String, Object[]>>... whereClauses)
            throws SQLException {

        QueryBuilder<T, UUID> queryBuilder = generateQueryBuilderForDataTable(columns, search, whereClauses);
        String columnName = columns.get(columnIndex).getName();
        queryBuilder.orderBy(columnName, "asc".equalsIgnoreCase(orderDir));
        queryBuilder.offset(start).limit(length);

        log.debug("DataTable query: {}", queryBuilder.prepareStatementString());
        return queryBuilder.query();
    }

    @SafeVarargs
    public final long countForDataTable(
            List<DataTableRequestDTO.Column> columns,
            String search,
            Pair<String, Pair<String, Object[]>>... whereClauses)
            throws SQLException {
        QueryBuilder<T, UUID> queryBuilder = generateQueryBuilderForDataTable(columns, search, whereClauses);
        log.debug("DataTable count query: {}", queryBuilder.prepareStatementString());
        return queryBuilder.countOf();
    }

    @NotNull @SafeVarargs
    private QueryBuilder<T, UUID> generateQueryBuilderForDataTable(
            @NotNull List<DataTableRequestDTO.Column> columns,
            String search,
            Pair<String, Pair<String, Object[]>>... whereClauses)
            throws SQLException {
        QueryBuilder<T, UUID> queryBuilder = dao.queryBuilder();
        Where<T, UUID> where = queryBuilder.where();

        int count = addSearchClauses(where, columns, search);
        count += addWhereClauses(where, whereClauses);

        if (count > 0) {
            where.and(count);
        }

        return queryBuilder;
    }

    private int addSearchClauses(Where<T, UUID> where, List<DataTableRequestDTO.Column> columns, String search)
            throws SQLException {
        int count = 0;
        for (DataTableRequestDTO.Column column : columns) {
            if (dao.getTableInfo().hasColumnName(column.getName()) && column.isSearchable()) {
                where.like(column.getName(), "%" + search + "%");
                count++;
            }
        }
        if (count > 0) {
            where.or(count);
        }
        return count;
    }

    private int addWhereClauses(Where<T, UUID> where, Pair<String, Pair<String, Object[]>>... whereClauses)
            throws SQLException {
        int count = 0;
        for (Pair<String, Pair<String, Object[]>> whereClause : whereClauses) {
            String columnName = whereClause.getValue().getKey();
            Object[] values = whereClause.getValue().getValue();

            if (dao.getTableInfo().hasColumnName(columnName)) {
                String operator = whereClause.getKey();
                switch (operator) {
                    case "in":
                        where.and().in(columnName, values);
                        count++;
                        break;
                    case "not_in":
                        where.and().notIn(columnName, values);
                        count++;
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported where clause: " + operator);
                }
            }
        }
        return count;
    }

    private void setCreatedAt(T entity) {
        try {
            Method method = entity.getClass().getMethod("setCreatedAt", Date.class);
            method.invoke(entity, new Date());
        } catch (Exception e) {
            log.trace("Failed to set createdAt using reflection", e);
        }
    }

    private void setId(T entity, UUID id) {
        try {
            Method method = entity.getClass().getMethod("setId", UUID.class);
            method.invoke(entity, id);
        } catch (Exception e) {
            log.trace("Failed to set ID using reflection", e);
        }
    }
}
