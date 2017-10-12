package io.mateu.ui.core.rest;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by miguel on 23/7/17.
 */
@Provider
public class Converter1 implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.getName().equals(LocalDate.class.getName())) {
            return new ParamConverter<T>() {

                @Override
                public T fromString(String value) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
                    //formatter = formatter.withLocale( putAppropriateLocaleHere );  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
                    LocalDate date = LocalDate.parse(value, formatter);
                    return rawType.cast(date);
                }

                @Override
                public String toString(T value) {
                    if (value == null) {
                        return null;
                    }
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
                    return ((LocalDate)value).format(formatter);
                }
            };
        }
        if (rawType.getName().equals(LocalDateTime.class.getName())) {
            return new ParamConverter<T>() {

                @Override
                public T fromString(String value) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm");
                    //formatter = formatter.withLocale( putAppropriateLocaleHere );  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
                    LocalDateTime date = LocalDateTime.parse(value, formatter);
                    return rawType.cast(date);
                }

                @Override
                public String toString(T value) {
                    if (value == null) {
                        return null;
                    }
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm");
                    return ((LocalDateTime)value).format(formatter);
                }
            };
        }
        return null;
    }
}
