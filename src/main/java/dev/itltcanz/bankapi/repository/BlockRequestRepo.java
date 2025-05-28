package dev.itltcanz.bankapi.repository;

import dev.itltcanz.bankapi.entity.BlockRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BlockRequestRepo extends JpaRepository<BlockRequest, UUID> {
}
