/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@DatabaseTable(tableName = "nodes")
public class Node implements Serializable {

    @DatabaseField(id = true, canBeNull = false)
    private UUID id;

    @DatabaseField(columnName = "active", canBeNull = false, defaultValue = "1")
    private boolean active;

    @NonNull @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true, columnName = "provider_id")
    private User provider;

    @NonNull @DatabaseField(canBeNull = false, unique = true)
    private String address;

    @DatabaseField(columnName = "flag_delete")
    private boolean flagDelete;

    @DatabaseField(columnName = "created_at", dataType = DataType.DATE_LONG)
    private Date createdAt;

    @DatabaseField(columnName = "updated_at", dataType = DataType.DATE_LONG)
    private Date updatedAt;
}
