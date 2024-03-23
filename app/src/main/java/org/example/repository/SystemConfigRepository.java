/* (C) 2024 */
package org.example.repository;

import com.j256.ormlite.stmt.SelectArg;
import java.sql.SQLException;
import java.util.Optional;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import org.example.entity.SystemConfig;

@NoArgsConstructor(onConstructor = @__(@Inject))
public class SystemConfigRepository extends AbstractCrudRepository<SystemConfig> {

    public Optional<SystemConfig> getByKey(String key) throws SQLException {
        return getDao().queryForEq("key", new SelectArg(key)).stream().findFirst();
    }
}
