package dev.itltcanz.bankapi.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class YearMonthMMYYSerializer extends StdSerializer<YearMonth> {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

    public YearMonthMMYYSerializer() {
        super(YearMonth.class);
    }
    @Override
    public void serialize(YearMonth yearMonth, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(yearMonth.format(formatter));
    }
}
