/* (C) 2024 */
package org.example.common;

import de.neuland.pug4j.exceptions.PugTemplateLoaderException;
import de.neuland.pug4j.template.TemplateLoader;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ClassTemplateLoader implements TemplateLoader {

    private final Charset encoding = StandardCharsets.UTF_8;

    @Getter
    private final String extension = "pug";

    private String basePath = "";

    public long getLastModified(String name) {
        return -1;
    }

    @Override
    public Reader getReader(String name) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Filename not provided!");
        }
        name = ensurePugExtension(name);
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(basePath + name);
        if (inputStream == null) {
            throw new PugTemplateLoaderException("Template not found: " + name);
        }
        return new InputStreamReader(inputStream, encoding);
    }

    private String ensurePugExtension(String templateName) {
        if (StringUtils.isBlank(FilenameUtils.getExtension(templateName))) {
            return templateName + "." + getExtension();
        }
        return templateName;
    }

    @Override
    public String getBase() {
        return basePath;
    }

    public void setBase(String basePath) {
        if (basePath.endsWith("/") || "".equals(basePath)) this.basePath = basePath;
        else this.basePath = basePath + "/";
    }
}
