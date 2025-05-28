package dev.itltcanz.bankapi.entity;

import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "cards")
@Getter
@Setter
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
    private CardStatus status;
    @Column(nullable = false)
    private BigDecimal balance;
}
