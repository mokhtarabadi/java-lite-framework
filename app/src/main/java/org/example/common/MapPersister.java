/* (C) 2023 */
package org.example.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
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
    public boolean isAppropriateId() {
        return false;
    }

    @Override
    public int getDefaultWidth() {
        return 0;
    }

    @Override
    public Class<?> getPrimaryClass() {
        return Map.class;
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        if (sqlArg == null) {
            return null;
        }
        return gson.fromJson((String) sqlArg, TYPE);
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        if (javaObject == null) {
            return null;
        }
        return gson.toJson(javaObject, TYPE);
    }
}
