package cz.clovekvtisni.coordinator.server.web.tag;

import cz.clovekvtisni.coordinator.server.web.format.MaDateFormat;
import cz.clovekvtisni.coordinator.server.web.format.MaDateFormatter;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 12/20/11
 * Time: 8:49 PM
 */
public class FormatTag extends HtmlEscapingAwareTag {

    private static final long serialVersionUID = -5272063213070962439L;
    private static final String REQUEST_DECIMAL_SEPARATOR = "ma.util.spring.www.tags.formatTag.decimalSeparator";
    private static final String REQUEST_GROUPING_SEPARATOR = "ma.util.spring.www.tags.formatTag.groupingSeparator";

    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private Object value;
    
    private String type;
    
    private String pattern;

    private Character decimalSeparator;

    private Character groupingSeparator;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    protected int doStartTagInternal() throws Exception {
        
        String formattedValue = null;

        if (value != null) {
            if (ValueTool.isEmpty(type) && ValueTool.isEmpty(pattern)) {
                ConversionService conversionService = (ConversionService) pageContext.getRequest().getAttribute(ConversionService.class.getName());
                if (conversionService != null && conversionService.canConvert(value.getClass(), String.class)) {
                    formattedValue = conversionService.convert(value, String.class);
                }
            }

            if (formattedValue == null) {
                if (value instanceof Date) {
                    if (!ValueTool.isEmpty(pattern)) {
                        formattedValue = new SimpleDateFormat(pattern).format((Date)value);
                    }
                    else if (!ValueTool.isEmpty(type)) {
                        MaDateFormat.Style style = null;
                        if ("datetime".equals(type)) {
                            style = MaDateFormat.Style.DATE_TIME;
                        }
                        else if ("date".equals(type) || "time".equals(type)) {
                            style = MaDateFormat.Style.valueOf(type.toUpperCase());
                        }
                        if (style != null) {
                            formattedValue = new MaDateFormatter(style, getMessageSource()).print((Date) value, getRequestContext().getLocale());
                        }
                    }
                    else if (!ValueTool.isEmpty(pattern)) {
                        formattedValue = new SimpleDateFormat(pattern).format((Date)value);
                    }
                }
                else if (value instanceof Number) {
                    if (!ValueTool.isEmpty(pattern)) {
                        DecimalFormat decimalFormat = new DecimalFormat(pattern);
                        DecimalFormatSymbols formatSymbols = DecimalFormatSymbols.getInstance(getRequestContext().getLocale());
                        Character decimalSeparator = this.decimalSeparator;
                        if (decimalSeparator == null) {
                            decimalSeparator = (Character) pageContext.getRequest().getAttribute(REQUEST_DECIMAL_SEPARATOR);
                        }
                        Character groupingSeparator = this.groupingSeparator;
                        if (groupingSeparator == null) {
                            groupingSeparator = (Character) pageContext.getRequest().getAttribute(REQUEST_GROUPING_SEPARATOR);
                        }
                        if (decimalSeparator != null) {
                            formatSymbols.setDecimalSeparator(decimalSeparator);
                        }
                        if (groupingSeparator != null) {
                            formatSymbols.setGroupingSeparator(groupingSeparator);
                        }
                        decimalFormat.setDecimalFormatSymbols(formatSymbols);
                        formattedValue = decimalFormat.format(value);
                    }
                }
            }

            if (formattedValue == null) {
                formattedValue = value.toString();
            }
        }

        if (formattedValue != null) {
            if (isHtmlEscape()) {
                formattedValue = HtmlUtils.htmlEscape(formattedValue);
            }
            pageContext.getOut().write(formattedValue);
        }

        return SKIP_BODY;
    }

    private MessageSource getMessageSource() {
        return getRequestContext().getMessageSource();
    }

    public Character getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(Character decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public Character getGroupingSeparator() {
        return groupingSeparator;
    }

    public void setGroupingSeparator(Character groupingSeparator) {
        this.groupingSeparator = groupingSeparator;
    }

    public static void setRequestDecimalSeparator(HttpServletRequest request, Character separator) {
        request.setAttribute(REQUEST_DECIMAL_SEPARATOR, separator);
    }
    public static void setRequestGroupingSeparator(HttpServletRequest request, Character separator) {
        request.setAttribute(REQUEST_GROUPING_SEPARATOR, separator);
    }
}
