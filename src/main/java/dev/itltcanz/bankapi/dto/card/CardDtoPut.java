package dev.itltcanz.bankapi.dto.card;

import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDtoPut {
    @NotBlank(message = "The owner is required")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", message = "Invalid UUID format")
    private String ownerId;
    @Pattern(regexp = "^(0[1-9]|1[0-2])/([0-9]{2})$", message = "Format: MM/yy")
    private String validityPeriod;
    private CardStatus status;
    @PositiveOrZero
    private BigDecimal balance;
}
