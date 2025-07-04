package dev.itltcanz.bankapi.repository;

import dev.itltcanz.bankapi.entity.User;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  @NonNull
  Page<User> findAll(@NonNull Pageable pageable);
}
