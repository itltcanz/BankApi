package dev.itltcanz.bankapi.dto.user;

import dev.itltcanz.bankapi.entity.enumeration.Role;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for partially updating a user.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoPatch implements UserDto {

  @Nullable
  private String username;
  @Nullable
  private String password;
  @Nullable
  private Role role;
}