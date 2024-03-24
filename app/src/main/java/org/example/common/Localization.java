/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.common;

import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.config.AppConfig;
import org.jetbrains.annotations.Nullable;
import spark.Request;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class Localization {

    @NonNull private AppConfig appConfig;

    private static final HashMap<String, ResourceBundle> BUNDLES = new HashMap<>();

    static {
        // en_US
        Locale locale = new Locale("en", "US");
        BUNDLES.put("en-US", ResourceBundle.getBundle("locale/messages", locale));

        // fa_IR
        locale = new Locale("fa", "IR");
        BUNDLES.put("fa-IR", ResourceBundle.getBundle("locale/messages", locale));

        // zh_CN
        locale = new Locale("zh", "CN");
        BUNDLES.put("zh-CN", ResourceBundle.getBundle("locale/messages", locale));
    }

    public List<String> getSupportedLocales() {
        return BUNDLES.keySet().stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    public ResourceBundle getBundle(String locale) {
        ResourceBundle bundle;
        if (locale != null && BUNDLES.containsKey(locale)) {
            bundle = BUNDLES.get(locale);
        } else {
            bundle = BUNDLES.get(appConfig.getDefaultLocale());
        }
        return bundle;
    }

    public String getString(@Nullable String locale, String key) {
        try {
            ResourceBundle bundle = getBundle(locale);
            log.trace("locale: {}, key: {}", bundle.getLocale(), key);
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public String getString(Request request, String key) {
        return getString(extractLocale(request), key);
    }

    public String getFormattedString(String locale, String key, Object... args) {
        return String.format(getString(locale, key), args);
    }

    public String getFormattedString(Request request, String key, Object... args) {
        return getFormattedString(extractLocale(request), key, args);
    }

    private String extractLocale(Request request) {
        return request.cookie("lang");
    }
}
