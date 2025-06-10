package dev.itltcanz.bankapi.filter;

import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class CardFilter {
  private CardStatus status;
  private String number;
  private String ownerId;
}
