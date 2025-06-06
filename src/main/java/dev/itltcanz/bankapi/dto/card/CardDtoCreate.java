package dev.itltcanz.bankapi.dto.card;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.itltcanz.bankapi.util.FutureOrPresentYearMonth;
import dev.itltcanz.bankapi.util.YearMonthMMYYDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for creating a new card.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CardDtoCreate implements CardDto {

  @NotBlank(message = "The owner is required")
  @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", message = "Invalid UUID format")
  private String ownerId;
  @JsonDeserialize(using = YearMonthMMYYDeserializer.class)
  @FutureOrPresentYearMonth
  private YearMonth validityPeriod;
  @PositiveOrZero
  private BigDecimal balance;
}
