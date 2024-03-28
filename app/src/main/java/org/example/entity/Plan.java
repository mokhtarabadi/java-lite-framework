package org.example.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@DatabaseTable(tableName = "plans")
public class Plan implements Serializable {

    @DatabaseField(id = true, canBeNull = false)
    private UUID id;

    @NonNull
    @DatabaseField(columnName = "name", canBeNull = false, unique = true)
    private String name;

    @DatabaseField(columnName = "created_at", dataType = DataType.DATE_LONG)
    private Date createdAt;

    @DatabaseField(columnName = "updated_at", dataType = DataType.DATE_LONG)
    private Date updatedAt;
}