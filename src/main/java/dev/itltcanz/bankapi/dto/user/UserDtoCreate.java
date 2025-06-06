package dev.itltcanz.bankapi.dto.user;

import dev.itltcanz.bankapi.entity.enumeration.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for creating a new user.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoCreate implements UserDto {

  @NotBlank
  @Size(min = 3, max = 30)
  private String username;

  @NotBlank
  @Size(min = 8, max = 30)
  private String password;

  @NotNull
  private Role role;
}