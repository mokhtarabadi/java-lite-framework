/* (C) 2023 */
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

    public List<Log> getByType(Log.Type type) throws SQLException {
        return getDao().queryForEq("type", type);
    }

    public List<Log> getByTypeAndUserId(UUID userId, int limit, @NotNull Log.Type... types) throws SQLException {
        String rawQuery =
                "SELECT * FROM logs WHERE type IN (%s) AND JSON_EXTRACT(data, '$.user_id') = ? ORDER BY created_at DESC LIMIT ?";

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            builder.append("?");
            builder.append(",");
        }
        builder.setLength(builder.length() - 1);
        rawQuery = String.format(rawQuery, builder.toString());

        List<String> params = new ArrayList<>();
        for (Log.Type type : types) {
            params.add(type.toString());
        }
        params.add(userId.toString());
        params.add(String.valueOf(limit));

        // log query
        log.debug("get by type and user id query: {}", rawQuery);

        try (GenericRawResults<String[]> results = getDao().queryRaw(rawQuery, params.toArray(new String[0]))) {
            return results.getResults().stream()
                    .map(LogMapper.INSTANCE::mapFromRawQuery)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to close results", e);
            throw new SQLException(e);
        }
    }

    public long countByTypes(Log.Type[] types) throws SQLException {
        QueryBuilder<Log, UUID> builder = getDao().queryBuilder();
        builder.where().in("type", (Object[]) types);
        return builder.countOf();
    }
}
