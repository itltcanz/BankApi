package dev.itltcanz.bankapi.dto.transaction;

import dev.itltcanz.bankapi.entity.enumeration.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDtoResponse {
    private String senderCardId;
    private String receiverCardId;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime createdAt = LocalDateTime.now();
}
