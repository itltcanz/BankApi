package dev.itltcanz.bankapi.filter;

import dev.itltcanz.bankapi.entity.Card;
import org.springframework.data.jpa.domain.Specification;

public interface FilterApplier {

  Specification<Card> apply(CardFilter filter);
}
