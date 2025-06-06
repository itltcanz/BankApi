package dev.itltcanz.bankapi.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * JPA converter for mapping YearMonth to a String in the format MM/yy in the database.
 */
@Converter(autoApply = true)
public class YearMonthAttributeConverter implements AttributeConverter<YearMonth, String> {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

  /**
   * Converts a YearMonth to a String for database storage.
   *
   * @param yearMonth The YearMonth to convert.
   * @return The formatted String in MM/yy format, or {@code null} if yearMonth is null.
   */
  @Override
  public String convertToDatabaseColumn(YearMonth yearMonth) {
    return yearMonth != null ? yearMonth.format(formatter) : null;
  }

  /**
   * Converts a String from the database to a YearMonth.
   *
   * @param yearMonthString The String in MM/yy format.
   * @return The parsed YearMonth, or {@code null} if yearMonthString is null.
   */
  @Override
  public YearMonth convertToEntityAttribute(String yearMonthString) {
    return yearMonthString != null ? YearMonth.parse(yearMonthString, formatter) : null;
  }
}