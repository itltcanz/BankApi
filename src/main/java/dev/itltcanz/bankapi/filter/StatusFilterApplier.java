package dev.itltcanz.bankapi.filter;

import dev.itltcanz.bankapi.entity.Card;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StatusFilterApplier implements FilterApplier {

  @Override
  public Specification<Card> apply(CardFilter filter) {
    if (filter.getStatus() == null) {
      return null;
    }
    return (root, query, cb) ->
        cb.equal(root.get("status"), filter.getStatus());
  }
}
