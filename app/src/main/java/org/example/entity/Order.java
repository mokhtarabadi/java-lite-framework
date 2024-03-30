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
import com.j256.ormlite.field.types.EnumStringType;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.common.OrderStatus;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@DatabaseTable(tableName = "orders")
public class Order implements Serializable {

    @DatabaseField(id = true, canBeNull = false)
    private UUID id;

    @NonNull @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "plan_id", canBeNull = false)
    private Plan plan;

    @NonNull @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "application_id", canBeNull = false)
    private Application application;

    @NonNull @DatabaseField(
            columnName = "status",
            canBeNull = false,
            defaultValue = "pending",
            persisterClass = EnumStringType.class)
    private OrderStatus status;

    @DatabaseField(columnName = "created_at", dataType = DataType.DATE_LONG)
    private Date createdAt;

    @DatabaseField(columnName = "updated_at", dataType = DataType.DATE_LONG)
    private Date updatedAt;
}
