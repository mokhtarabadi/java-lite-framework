/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import com.j256.ormlite.support.DatabaseResults;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonMapType extends StringType {

    private static final JsonMapType singleTon = new JsonMapType();

    private static final Gson gson = new Gson();
    private static final Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

    public static JsonMapType getSingleton() {
        return singleTon;
    }

    private JsonMapType() {
        super(SqlType.STRING, new Class<?>[] {Map.class});
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) {
        log.debug("parse default string: {}", defaultStr);
        return gson.fromJson(defaultStr, mapType);
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        String jsonString = results.getString(columnPos);
        if (jsonString == null) {
            return null;
        }
        log.debug("result to sql arg: {}", jsonString);
        return gson.fromJson(jsonString, mapType);
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        if (javaObject == null) {
            return null;
        }
        log.debug("java to sql arg: {}", javaObject);
        return gson.toJson(javaObject);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        if (sqlArg == null) {
            return null;
        }
        log.debug("sql arg to java: {}", sqlArg);
        return gson.fromJson((String) sqlArg, mapType);
    }

    @Override
    public boolean isValidForField(Field field) {
        return Map.class.isAssignableFrom(field.getType());
    }
}
