package dev.itltcanz.bankapi.dto.card;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.itltcanz.bankapi.util.YearMonthMMYYSerializer;
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
    @JsonSerialize(using = YearMonthMMYYSerializer.class)
    private YearMonth validityPeriod;
    private BigDecimal balance;
    private String status;
}
