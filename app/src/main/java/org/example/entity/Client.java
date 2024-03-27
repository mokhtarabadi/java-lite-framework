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
@DatabaseTable(tableName = "clients")
public class Client implements Serializable {

    @DatabaseField(id = true, canBeNull = false)
    private UUID id;

    @NonNull
    @DatabaseField(columnName = "application_id", foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Application application;

    @NonNull
    @DatabaseField(columnName = "passwd", canBeNull = false, unique = true)
    private UUID passwd;

    @NonNull
    @DatabaseField(columnName = "total_traffic", canBeNull = false, defaultValue = "0")
    private Long totalTraffic;

    @NonNull
    @DatabaseField(columnName = "used_traffic", canBeNull = false, defaultValue = "0")
    private Long usedTraffic;

    @DatabaseField(columnName = "expiry_date", dataType = DataType.DATE_LONG)
    private Date expiryDate;

    @DatabaseField(columnName = "created_at", dataType = DataType.DATE_LONG)
    private Date createdAt;

    @DatabaseField(columnName = "updated_at", dataType = DataType.DATE_LONG)
    private Date updatedAt;
}
