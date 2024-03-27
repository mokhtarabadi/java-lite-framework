package org.example.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import jodd.cli.Cli;
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
@DatabaseTable(tableName = "sessions")
public class Session implements Serializable {

    @DatabaseField(id = true, canBeNull = false)
    private UUID id;

    @NonNull
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "client_id", canBeNull = false)
    private Client client;

    @NonNull
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "node_id", canBeNull = false)
    private Node node;

    @DatabaseField(columnName = "created_at", dataType = DataType.DATE_LONG)
    private Date createdAt;

    @DatabaseField(columnName = "updated_at", dataType = DataType.DATE_LONG)
    private Date updatedAt;
}
