package dev.itltcanz.bankapi.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class YearMonthMMYYDeserializer extends StdDeserializer<YearMonth> {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

    public YearMonthMMYYDeserializer() {
        super(YearMonth.class);
    }

    @Override
    public YearMonth deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String yearMonthString = jsonParser.getText();
        try {
            return YearMonth.parse(yearMonthString, formatter);
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new IOException("Failed to parse YearMonth from '" + yearMonthString + "'. Expected format: MM/yy", e);
        }
    }
}
