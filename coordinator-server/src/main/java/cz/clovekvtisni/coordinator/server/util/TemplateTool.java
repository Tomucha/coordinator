package cz.clovekvtisni.coordinator.server.util;

import freemarker.core.ArithmeticEngine;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 1/18/11
 * Time: 11:43 AM
 */
public class TemplateTool {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    private static final TemplateTool instance = new TemplateTool();

    private final Configuration config = new Configuration();

    private static final String TEMPLATE_ROOT_PATH = "/email/";

    private TemplateTool() {
        config.setArithmeticEngine(ArithmeticEngine.BIGDECIMAL_ENGINE);
        final URL url = this.getClass().getResource(TEMPLATE_ROOT_PATH);
        if (url == null) {
			throw new IllegalArgumentException("Couldn't find resource base directory: " + TEMPLATE_ROOT_PATH);
		}
        final File templateDir = new File(url.getFile());
        try {
            config.setDirectoryForTemplateLoading(templateDir);
        } catch (IOException e) {
            throw new IllegalStateException("Templates directory " + TEMPLATE_ROOT_PATH + " couldn't be set to template processor", e);
        }
    }

    public static TemplateTool getInstance() {
        return instance;
    }

    public String processTemplate(final String templateName, Map<String, Object> context, Locale locale) throws IOException, TemplateException {
       final Template template = getTemplateFromName(templateName, locale);
        log.fine("Processing template: " + templateName);
        final StringWriter writer = new StringWriter();
        template.process(context, writer);
        return writer.toString();
    }

    private Template getTemplateFromName(String templateName, Locale locale) throws IOException {
        return config.getTemplate(templateName + ".ftl", locale, "UTF-8");
    }
}
