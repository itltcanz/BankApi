package dev.itltcanz.bankapi.dto.transaction;

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
public class TransactionDtoCreate {
    @NotBlank
    @Pattern(regexp = "^[0-9]{16}", message = "Invalid card number format")
    private String senderCardId;
    @NotBlank
    @Pattern(regexp = "^[0-9]{16}", message = "Invalid card number format")
    private String receiverCardId;
    @PositiveOrZero
    private BigDecimal amount;
}
