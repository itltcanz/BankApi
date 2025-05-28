package dev.itltcanz.bankapi.repository;

import dev.itltcanz.bankapi.entity.Card;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepo extends JpaRepository<Card, String> {

    @NonNull
    Page<Card> findAll(@NonNull Pageable pageable);

}
