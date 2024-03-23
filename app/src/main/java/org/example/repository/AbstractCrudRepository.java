/* (C) 2023 */
package org.example.repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
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

    // create
    public UUID create(T entity) throws SQLException {
        try {
            Method setCreatedAtMethod = entity.getClass().getMethod("setCreatedAt", Date.class);
            Date currentDate = new Date();
            setCreatedAtMethod.invoke(entity, currentDate);
        } catch (Exception e) {
            log.trace("failed call createdAt using reflection", e);
        }

        UUID randomUuid = UUID.randomUUID();
        try {
            Method setIdMethod = entity.getClass().getMethod("setId", UUID.class);
            setIdMethod.invoke(entity, randomUuid);
        } catch (Exception e) {
            log.trace("failed to call setId using reflection", e);
        }
        dao.create(entity);
        return randomUuid;
    }

    // read
    public T read(UUID id) throws SQLException {
        return dao.queryForId(id);
    }

    // update
    public int update(T entity) throws SQLException {
        return dao.update(entity);
    }

    // delete
    public int delete(T entity) throws SQLException {
        return dao.delete(entity);
    }

    // count
    public long count() throws SQLException {
        return dao.countOf();
    }

    // all
    public List<T> all() throws SQLException {
        return dao.queryForAll();
    }

    // batch
    public <V> V batch(Callable<V> callable) throws Exception {
        return dao.callBatchTasks(callable);
    }

    // delete by id
    public int deleteById(UUID uuid) throws SQLException {
        DeleteBuilder<T, UUID> builder = dao.deleteBuilder();
        builder.where().eq("id", uuid);
        return builder.delete();
    }

    // datatables
    @SafeVarargs
    public final List<T> queryForDataTable(
            long start,
            long length,
            List<DataTableRequestDTO.Column> columns,
            String search,
            int columnIndex,
            @NotNull String orderDir,
            Pair<String, Pair<String, Object>>... whereClauses)
            throws SQLException {

        QueryBuilder<T, UUID> queryBuilder = generateQueryBuilderForDataTable(columns, search, whereClauses);

        String columnName = columns.get(columnIndex).getName();
        queryBuilder.orderBy(columnName, orderDir.equalsIgnoreCase("asc"));

        queryBuilder.offset(start);
        queryBuilder.limit(length);

        // log the query
        log.debug("DataTable query: {}", queryBuilder.prepareStatementString());

        return queryBuilder.query();
    }

    @SafeVarargs
    public final long countForDataTable(
            List<DataTableRequestDTO.Column> columns, String search, Pair<String, Pair<String, Object>>... whereClauses)
            throws SQLException {
        QueryBuilder<T, UUID> queryBuilder = generateQueryBuilderForDataTable(columns, search, whereClauses);

        // log the query
        log.debug("DataTable count query: {}", queryBuilder.prepareStatementString());

        return queryBuilder.countOf();
    }

    @NotNull @SafeVarargs
    private QueryBuilder<T, UUID> generateQueryBuilderForDataTable(
            @NotNull List<DataTableRequestDTO.Column> columns,
            String search,
            Pair<String, Pair<String, Object>>... whereClauses)
            throws SQLException {
        QueryBuilder<T, UUID> queryBuilder = getDao().queryBuilder();

        Where<T, UUID> where = queryBuilder.where();

        int count = 0;
        for (DataTableRequestDTO.Column column : columns) {
            if (getDao().getTableInfo().hasColumnName(column.getName()) && column.isSearchable()) {
                where.like(column.getName(), new SelectArg("%" + search + "%"));
                count++;
            }
        }

        // add all where clauses with or
        if (count > 0) {
            where.or(count);
        }

        // add other where clauses
        count = 0;
        for (Pair<String, Pair<String, Object>> whereClause : whereClauses) {
            String columnName = whereClause.getValue().getKey();
            SelectArg[] values = Arrays.stream((Object[]) whereClause.getValue().getValue())
                    .map(SelectArg::new)
                    .toArray(SelectArg[]::new);

            if (getDao().getTableInfo().hasColumnName(columnName)) {
                String operator = whereClause.getKey();
                switch (operator) {
                    case "in":
                        where.in(columnName, (Object[]) values);
                        count++;
                        break;
                    case "not_in":
                        where.notIn(columnName, (Object[]) values);
                        count++;
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported where clause: " + operator);
                }
            }
        }

        // add all where clauses with and
        if (count > 0) {
            where.and(count);
        }

        return queryBuilder;
    }
}
