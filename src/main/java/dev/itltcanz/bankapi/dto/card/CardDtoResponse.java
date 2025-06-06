package dev.itltcanz.bankapi.dto.card;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.itltcanz.bankapi.util.YearMonthMMYYSerializer;
import java.math.BigDecimal;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for representing card details in responses.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CardDtoResponse implements CardDto {

  private String number;
  private String ownerId;
  @JsonSerialize(using = YearMonthMMYYSerializer.class)
  private YearMonth validityPeriod;
  private BigDecimal balance;
  private String status;
}
