package cz.clovekvtisni.coordinator.server.web.format;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 2/29/12
 * Time: 3:11 PM
 */
public class MaStringToNumberConverterFactory implements ConverterFactory<String, Number> {

    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumber<T>(targetType);
    }

    private static final class StringToNumber<T extends Number> implements Converter<String, T> {

        private final Class<T> targetType;

        public StringToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        public T convert(String source) {
            if (source.length() == 0) {
                return null;
            }

            String normalizedNumber = StringUtils.trimAllWhitespace(source).replaceFirst("^\\+", "");
            if (normalizedNumber.indexOf('.') < 0) {
                int i = normalizedNumber.lastIndexOf(',');
                if (i > 0 && i < normalizedNumber.length() - 1) {
                    normalizedNumber = normalizedNumber.substring(0, i) + '.' + normalizedNumber.substring(i+1);
                }
            }
            return NumberUtils.parseNumber(normalizedNumber, this.targetType);
        }
    }

}
