package dev.itltcanz.bankapi.dto.transaction;

import dev.itltcanz.bankapi.entity.enumeration.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for representing transaction details in responses.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDtoResponse {

  private String id;
  private String senderCardId;
  private String receiverCardId;
  private BigDecimal amount;
  private TransactionStatus status;
  private LocalDateTime createdAt;
}
