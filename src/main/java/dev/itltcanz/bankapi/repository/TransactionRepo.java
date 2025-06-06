package dev.itltcanz.bankapi.repository;

import dev.itltcanz.bankapi.entity.Transaction;
import dev.itltcanz.bankapi.entity.User;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<Transaction, UUID> {

  Page<Transaction> findTransactionsBySenderCard_Owner(User senderCardOwner, Pageable pageable);
}
