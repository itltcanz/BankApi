package dev.itltcanz.bankapi.repository;

import dev.itltcanz.bankapi.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CardRepo extends JpaRepository<Card, String>, JpaSpecificationExecutor<Card> {

  boolean existsByNumber(String number);

}
