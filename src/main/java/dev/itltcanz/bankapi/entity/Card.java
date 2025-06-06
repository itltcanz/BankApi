package dev.itltcanz.bankapi.entity;

import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "cards")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Card {

  @Id
  @Column(updatable = false)
  private String number;
  @ManyToOne
  private User owner;
  @Column(nullable = false)
  private YearMonth validityPeriod;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CardStatus status;
  @Column(nullable = false)
  private BigDecimal balance;
}
