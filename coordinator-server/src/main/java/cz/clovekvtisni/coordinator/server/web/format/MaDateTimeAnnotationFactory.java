package cz.clovekvtisni.coordinator.server.web.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 12/20/11
 * Time: 9:41 AM
 */
public class MaDateTimeAnnotationFactory implements AnnotationFormatterFactory<MaDateFormat>{

    @Autowired
    private MessageSource messageSource;

    @Override
    public Set<Class<?>> getFieldTypes() {
        HashSet<Class<?>> fieldTypes = new HashSet<Class<?>>();
        fieldTypes.add(Date.class);
        return fieldTypes;
    }

    @Override
    public Printer<?> getPrinter(MaDateFormat annotation, Class<?> fieldType) {
        return configureFormatter(annotation, fieldType);
    }

    @Override
    public Parser<?> getParser(MaDateFormat annotation, Class<?> fieldType) {
        return configureFormatter(annotation, fieldType);
    }

    protected Formatter<Date> configureFormatter(MaDateFormat annotation, Class<?> fieldType) {
        return new MaDateFormatter(annotation, messageSource);
    }

}
