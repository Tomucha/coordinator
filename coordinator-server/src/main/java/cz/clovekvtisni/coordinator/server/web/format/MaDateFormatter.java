/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.clovekvtisni.coordinator.server.web.format;

import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A formatter for {@link java.util.Date} types.
 * Allows the configuration of an explicit date pattern and locale.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 * @see java.text.SimpleDateFormat
 */
public class MaDateFormatter implements Formatter<Date> {

    private MessageSource messageSource;

    private String pattern;
    
    private MaDateFormat.Style style;

    public MaDateFormatter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public MaDateFormatter(MaDateFormat annotation, MessageSource messageSource) {
        this.messageSource = messageSource;
        if (annotation != null) {
            style = annotation.style();
            pattern = annotation.pattern();
        }
    }

    public MaDateFormatter(MaDateFormat.Style style, MessageSource messageSource) {
        this.style = style;
        this.messageSource = messageSource;
    }

    public String print(Date date, Locale locale) {
		return getDateFormat(locale).format(date);
	}

	public Date parse(String text, Locale locale) throws ParseException {
		return getDateFormat(locale).parse(text);
	}


	protected DateFormat getDateFormat(Locale locale) {
		DateFormat dateFormat;
        String pattern;
        if (this.pattern == null && style == null) {
            pattern = messageSource.getMessage("format." + MaDateFormat.Style.DATE_TIME, new Object[0], locale);
        }
        else if (!ValueTool.isEmpty(this.pattern)) {
            pattern = this.pattern;
        }
        else {
            pattern = messageSource.getMessage("format." + style, new Object[0], locale);
        }
        dateFormat = new SimpleDateFormat(pattern, locale);
		return dateFormat;
	}
}
