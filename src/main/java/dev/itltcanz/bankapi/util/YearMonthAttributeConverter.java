package dev.itltcanz.bankapi.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
public class YearMonthAttributeConverter implements AttributeConverter<YearMonth, String> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
    @Override
    public String convertToDatabaseColumn(YearMonth yearMonth) {
        return yearMonth != null ? yearMonth.format(formatter) : null;
    }

    @Override
    public YearMonth convertToEntityAttribute(String yearMonthString) {
        return yearMonthString != null ? YearMonth.parse(yearMonthString, formatter) : null;
    }
}
