/* (C) 2023 */
package org.example.common;

import de.neuland.pug4j.PugConfiguration;
import de.neuland.pug4j.template.PugTemplate;
import java.io.IOException;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import spark.ModelAndView;
import spark.TemplateEngine;

@Getter
@Slf4j
public class PugTemplateEngine extends TemplateEngine {

    private final PugConfiguration configuration;

    public PugTemplateEngine(String templateRoot) {
        // remove start slash
        if (templateRoot.startsWith("/")) {
            templateRoot = templateRoot.substring(1);
        }

        ClassTemplateLoader templateLoader = new ClassTemplateLoader();
        templateLoader.setBase(templateRoot);

        configuration = new PugConfiguration();
        configuration.setTemplateLoader(templateLoader);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public String render(ModelAndView modelAndView) {
        try {
            PugTemplate template = configuration.getTemplate(modelAndView.getViewName());
            return configuration.renderTemplate(template, (Map<String, Object>) modelAndView.getModel());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
