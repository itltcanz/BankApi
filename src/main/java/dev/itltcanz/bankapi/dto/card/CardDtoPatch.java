package dev.itltcanz.bankapi.dto.card;

import dev.itltcanz.bankapi.entity.enumeration.CardStatus;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDtoPatch {
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", message = "Invalid UUID format")
    @Nullable
    private String ownerId;
    @Pattern(regexp = "^(0[1-9]|1[0-2])/([0-9]{2})$", message = "Format: MM/yy")
    @Nullable
    private String validityPeriod;
    @Nullable
    private CardStatus status;
    @Nullable
    private BigDecimal balance;
}
