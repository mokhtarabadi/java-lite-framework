/* (C) 2024 */
package org.example.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@DatabaseTable(tableName = "configs")
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class SystemConfig {

    @DatabaseField(id = true, canBeNull = false)
    private UUID id;

    @NonNull @DatabaseField(canBeNull = false, unique = true)
    private String key;

    @NonNull @DatabaseField(canBeNull = false)
    private String value;

    @DatabaseField(columnName = "created_at", dataType = DataType.DATE_LONG)
    private Date createdAt;

    @DatabaseField(columnName = "updated_at", dataType = DataType.DATE_LONG)
    private Date updatedAt;
}
