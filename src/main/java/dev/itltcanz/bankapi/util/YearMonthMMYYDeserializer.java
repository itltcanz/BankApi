package dev.itltcanz.bankapi.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.itltcanz.bankapi.exception.YearMonthParseException;
import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Jackson deserializer for parsing YearMonth from JSON strings in MM/yy format.
 */
public class YearMonthMMYYDeserializer extends StdDeserializer<YearMonth> {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

  /**
   * Constructs a new YearMonthMMYYDeserializer.
   */
  public YearMonthMMYYDeserializer() {
    super(YearMonth.class);
  }

  /**
   * Deserializes a JSON string in MM/yy format to a YearMonth.
   *
   * @param jsonParser             The JSON parser.
   * @param deserializationContext The deserialization context.
   * @return The parsed YearMonth.
   * @throws IOException             If an I/O error occurs.
   * @throws YearMonthParseException If the string cannot be parsed to a valid YearMonth.
   */
  @Override
  public YearMonth deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException {
    String yearMonthString = jsonParser.getText();
    try {
      return YearMonth.parse(yearMonthString, formatter);
    } catch (DateTimeParseException | NumberFormatException e) {
      throw new YearMonthParseException(
          "Failed to parse YearMonth from '" + yearMonthString + "'. Expected format: MM/yy", e);
    }
  }
}