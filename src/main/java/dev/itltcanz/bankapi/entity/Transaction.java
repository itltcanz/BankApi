package dev.itltcanz.bankapi.entity;

import dev.itltcanz.bankapi.entity.enumeration.TransactionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "sender_card_id", nullable = false)
  private Card senderCard;

  @ManyToOne
  @JoinColumn(name = "receiver_card_id", nullable = false)
  private Card receiverCard;

  @Column(nullable = false, updatable = false)
  private BigDecimal amount;

  @Column(nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  private TransactionStatus status = TransactionStatus.PENDING;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
