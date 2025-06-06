package dev.itltcanz.bankapi.filter;

import dev.itltcanz.bankapi.entity.Card;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;


@Component
public class CardSpecification {

  private final List<FilterApplier> filterAppliers;

  @Autowired
  public CardSpecification(List<FilterApplier> filterAppliers) {
    this.filterAppliers = filterAppliers;
  }

  public Specification<Card> withFilter(CardFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      for (FilterApplier applier : filterAppliers) {
        Specification<Card> spec = applier.apply(filter);
        if (spec != null) {
          Predicate predicate = spec.toPredicate(root, query, cb);
          if (predicate != null) {
            predicates.add(predicate);
          }
        }
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
