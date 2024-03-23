/* (C) 2023 */
package org.example.repository;

import com.j256.ormlite.stmt.DeleteBuilder;
import java.sql.SQLException;
import java.util.UUID;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import org.example.entity.UserRole;

@NoArgsConstructor(onConstructor = @__(@Inject))
public class UserRoleRepository extends AbstractCrudRepository<UserRole> {

    public int deleteByUserId(UUID userId) throws SQLException {
        DeleteBuilder<UserRole, UUID> builder = getDao().deleteBuilder();
        builder.where().eq("user_id", userId);
        return builder.delete();
    }
}
