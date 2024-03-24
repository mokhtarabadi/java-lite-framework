/* (C) 2023 */
package org.example.entity;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import java.util.UUID;
import lombok.*;

@DatabaseTable(tableName = "users")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class User {

    @DatabaseField(id = true, canBeNull = false)
    private UUID id;

    @NonNull @DatabaseField(columnName = "username", canBeNull = false, uniqueIndex = true)
    private String username;

    @DatabaseField(columnName = "email", index = true)
    private String email;

    @NonNull @DatabaseField(columnName = "first_name", canBeNull = false)
    private String firstName;

    @DatabaseField(columnName = "last_name")
    private String lastName;

    @NonNull @DatabaseField(columnName = "password", canBeNull = false)
    private String password;

    @NonNull @DatabaseField(columnName = "class", canBeNull = false, defaultValue = "0")
    private Integer clazz;

    @NonNull @DatabaseField(columnName = "active", canBeNull = false, defaultValue = "1")
    private Boolean active;

    @DatabaseField(columnName = "created_at", dataType = DataType.DATE_LONG)
    private Date createdAt;

    @DatabaseField(columnName = "updated_at", dataType = DataType.DATE_LONG)
    private Date updatedAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ForeignCollectionField(eager = true)
    private ForeignCollection<UserRole> userRoles;

    // utility methods
    public boolean isAdmin() {
        return userRoles.stream()
                .anyMatch(userRole -> userRole.getRole().getName().equals("ROLE_ADMIN"));
    }

    public boolean isActive() {
        return active;
    }
}