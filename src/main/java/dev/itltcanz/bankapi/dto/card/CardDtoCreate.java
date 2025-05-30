package dev.itltcanz.bankapi.dto.card;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.itltcanz.bankapi.util.YearMonthMMYYDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDtoCreate {
    @NotBlank(message = "The owner is required")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", message = "Invalid UUID format")
    private String ownerId;
    @JsonDeserialize(using = YearMonthMMYYDeserializer.class)
    private YearMonth validityPeriod;
    @PositiveOrZero
    private BigDecimal balance;
}
