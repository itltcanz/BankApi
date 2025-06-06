package dev.itltcanz.bankapi.repository;

import dev.itltcanz.bankapi.entity.BlockRequest;
import dev.itltcanz.bankapi.entity.User;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRequestRepo extends JpaRepository<BlockRequest, UUID> {

  Page<BlockRequest> findAllByUser(User user, Pageable pageable);
}
