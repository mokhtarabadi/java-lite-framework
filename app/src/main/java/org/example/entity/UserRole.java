/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@DatabaseTable(tableName = "user_roles")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class UserRole implements Serializable {

    @DatabaseField(id = true, canBeNull = false)
    private UUID id;

    @NonNull @DatabaseField(foreign = true, columnName = "user_id", uniqueCombo = true, canBeNull = false)
    private User user;

    @NonNull @DatabaseField(
            foreign = true,
            foreignAutoRefresh = true,
            columnName = "role_id",
            uniqueCombo = true,
            canBeNull = false)
    private Role role;
}
