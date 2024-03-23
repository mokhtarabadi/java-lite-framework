/* (C) 2023 */
package org.example.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.example.config.AppConfig;

public class Utility {

    @Inject
    protected AppConfig appConfig;

    @Inject
    protected Gson gson;

    private Utility() {}

    private static class InstanceHolder {
        private static final Utility INSTANCE = new Utility();
    }

    public static Utility getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public HashMap<String, Object> toMap(String json) {
        Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public <T> T fromLinkedTreeMap(Object obj, Class<T> clazz) {
        return gson.fromJson(gson.toJson(obj), clazz);
    }

    public String readFileFromResource(String path) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(path)) {
            return IOUtils.toString(resource, "UTF-8");
        }
    }
}
