package dev.itltcanz.bankapi.repository;

import dev.itltcanz.bankapi.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepo extends JpaRepository<Transaction, UUID> {
}
