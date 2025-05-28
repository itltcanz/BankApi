package dev.itltcanz.bankapi.dto.card;

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
    @Pattern(regexp = "^(0[1-9]|1[0-2])/([0-9]{2})$", message = "Format: MM/yy")
    private YearMonth validityPeriod;
    @PositiveOrZero
    private BigDecimal balance;
}
