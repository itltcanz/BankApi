package dev.itltcanz.bankapi.filter;

import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CardFilter {

  private CardStatus status;
  private String number;
  private String ownerId;
}
