package dev.itltcanz.bankapi.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Jackson serializer for converting YearMonth to JSON strings in MM/yy format.
 */
public class YearMonthMMYYSerializer extends StdSerializer<YearMonth> {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

  /**
   * Constructs a new YearMonthMMYYSerializer.
   */
  public YearMonthMMYYSerializer() {
    super(YearMonth.class);
  }

  /**
   * Serializes a YearMonth to a JSON string in MM/yy format.
   *
   * @param yearMonth          The YearMonth to serialize.
   * @param jsonGenerator      The JSON generator.
   * @param serializerProvider The serializer provider.
   * @throws IOException If an I/O error occurs.
   */
  @Override
  public void serialize(YearMonth yearMonth, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeString(yearMonth.format(formatter));
  }
}