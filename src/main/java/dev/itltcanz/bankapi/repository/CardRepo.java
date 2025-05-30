package dev.itltcanz.bankapi.repository;

import dev.itltcanz.bankapi.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepo extends JpaRepository<Card, String> {
    boolean existsByNumber(String number);

}
