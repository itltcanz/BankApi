package dev.itltcanz.bankapi.filter;

import dev.itltcanz.bankapi.entity.Card;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class OwnerFilterApplier implements FilterApplier {

  @Override
  public Specification<Card> apply(CardFilter filter) {
    if (filter.getOwnerId() == null) {
      return null;
    }
    return (root, query, cb) ->
        cb.equal(root.get("owner").get("id"), UUID.fromString(filter.getOwnerId()));
  }
}
