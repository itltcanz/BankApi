package dev.itltcanz.bankapi.dto.card;

import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
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
public class CardDtoResponse {
    private String number;
    private String ownerId;
    private YearMonth validityPeriod;
    private BigDecimal balance;
    private CardStatus status;
}
