package dev.itltcanz.bankapi.dto.user;

import dev.itltcanz.bankapi.entity.enumeration.Role;


/**
 * Interface for Data Transfer Objects (DTOs) representing user details.
 */
public interface UserDto {

  /**
   * Retrieves the username.
   *
   * @return The username as a string.
   */
  String getUsername();

  /**
   * Retrieves the password.
   *
   * @return The password as a string.
   */
  String getPassword();

  /**
   * Retrieves the user's role.
   *
   * @return The role as a Role enum.
   */
  Role getRole();
}