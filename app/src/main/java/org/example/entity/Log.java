/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.entity;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import lombok.*;
import org.example.common.JsonMapType;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@DatabaseTable(tableName = "logs")
public class Log implements Serializable {

    @Getter
    public enum Type {
        @SerializedName("new_user")
        NEW_USER("new_user"),

        @SerializedName("login")
        LOGIN("login"),

        @SerializedName("invalid_login")
        INVALID_LOGIN("invalid_login"),

        // role changed
        @SerializedName("role_changed")
        ROLE_CHANGED("role_changed");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static Type fromValue(String value) {
            for (Type type : Type.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("unknown log type: " + value);
        }
    }

    @DatabaseField(id = true, canBeNull = false)
    private UUID id;

    @NonNull @DatabaseField(dataType = DataType.ENUM_TO_STRING, canBeNull = false, index = true)
    private Type type;

    @NonNull @DatabaseField(canBeNull = false, persisterClass = JsonMapType.class)
    private Map<String, Object> data;

    @DatabaseField(columnName = "created_at", dataType = DataType.DATE_LONG)
    private Date createdAt;

    @DatabaseField(columnName = "updated_at", dataType = DataType.DATE_LONG)
    private Date updatedAt;
}
