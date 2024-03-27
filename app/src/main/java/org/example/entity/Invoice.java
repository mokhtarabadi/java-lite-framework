package org.example.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.common.MapPersister;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@DatabaseTable(tableName = "invoices")
public class Invoice implements Serializable {

    @DatabaseField(id = true, canBeNull = false)
    private UUID id;

    @NonNull
    @DatabaseField(persisterClass = MapPersister.class)
    private Map<String, Object> data; // <key, value>

    @DatabaseField(columnName = "created_at", dataType = DataType.DATE_LONG)
    private Date createdAt;

    @DatabaseField(columnName = "updated_at", dataType = DataType.DATE_LONG)
    private Date updatedAt;
}
