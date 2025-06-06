package dev.itltcanz.bankapi.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for user registration.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoRegistration {

  @NotBlank
  @Size(min = 3, max = 30)
  private String username;
  @NotBlank
  @Size(min = 8, max = 30)
  private String password;
}