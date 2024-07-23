/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import com.j256.ormlite.support.DatabaseResults;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapPersister extends StringType {

    private static final MapPersister singleTon = new MapPersister();

    private static final Type TYPE = new TypeToken<Map<String, Object>>() {}.getType();

    public static MapPersister getSingleton() {
        return singleTon;
    }

    private final Gson gson;

    private MapPersister() {
        super(SqlType.LONG_STRING);
        this.gson = new Gson();
        log.debug("no arg constructor");
    }

    /** Here for others to subclass. */
    protected MapPersister(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
        this.gson = new Gson();

        log.debug("arg constructor");
    }

    @Override
    public Class<?> getPrimaryClass() {
        return (Class<?>) TYPE;
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        String json = results.getString(columnPos);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, TYPE);
    }
}
