/* (C) 2024 */
package org.example.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.bval.jsr.ApacheValidationProvider;
import org.apache.bval.jsr.DefaultMessageInterpolator;
import org.example.config.AppConfig;
import spark.Request;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CustomValidator {

    @NonNull private Localization localization;

    @NonNull private AppConfig appConfig;

    private HashMap<String, Validator> validators;

    public void init() {
        validators = new HashMap<>();
        for (String supportedLocale : localization.getSupportedLocales()) {
            validators.put(supportedLocale, makeValidator(supportedLocale));
        }
    }

    public <T> List<String> validate(String locale, T dto) {
        Set<ConstraintViolation<T>> violations = getValidator(locale).validate(dto);
        if (!violations.isEmpty()) {
            return violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public <T> List<String> validate(Request request, T dto) {
        String locale = extractLocale(request);
        return validate(locale, dto);
    }

    private Validator getValidator(String locale) {
        Validator validator;
        if (validators.containsKey(locale)) {
            validator = validators.get(locale);
        } else {
            validator = validators.get(appConfig.getDefaultLocale());
        }
        return validator;
    }

    private Validator makeValidator(String locale) {
        @Cleanup
        ValidatorFactory validatorFactory = Validation.byProvider(ApacheValidationProvider.class)
                .configure()
                .messageInterpolator(new DefaultMessageInterpolator(localization.getBundle(locale)))
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    private String extractLocale(Request request) {
        return request.cookie("lang");
    }
}
