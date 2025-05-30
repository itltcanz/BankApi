package dev.itltcanz.bankapi.dto.card;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;

import dev.itltcanz.bankapi.util.YearMonthMMYYDeserializer;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
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
public class CardDtoPatch {
    @Nullable
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", message = "Invalid UUID format")
    private String ownerId;
    @Nullable
    @JsonDeserialize(using = YearMonthMMYYDeserializer.class)
    private YearMonth validityPeriod;
    @Nullable
    private CardStatus status;
    @Nullable
    private BigDecimal balance;
}
