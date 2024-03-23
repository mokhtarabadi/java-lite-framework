/* (C) 2023 */
package org.example.repository;

import com.j256.ormlite.stmt.SelectArg;
import java.sql.SQLException;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import org.example.entity.Role;

@NoArgsConstructor(onConstructor = @__(@Inject))
public class RoleRepository extends AbstractCrudRepository<Role> {

    public Role getByName(String name) throws SQLException {
        return getDao().queryBuilder().where().eq("name", new SelectArg(name)).queryForFirst();
    }
}
