package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.user.UserDtoRegistration;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.exception.UsernameAlreadyUseException;

/**
 * The {@code AuthService} interface defines methods for authenticating and logging in users.
 * Implementations of this interface handle user login and authentication.
 */
public interface AuthService {

  /**
   * Retrieves the currently authenticated user from the security context.
   *
   * @return The authenticated User entity.
   * @throws NotFoundException if the user is not found in the repository.
   */
  User getCurrentUser();

  /**
   * Registers a new user with the provided credentials.
   *
   * @param userDto The user registration details.
   * @return The registered user's details as a DTO.
   * @throws UsernameAlreadyUseException if the username is already taken.
   */
  UserDtoResponse register(UserDtoRegistration userDto);

  /**
   * Verifies user credentials using the authentication manager.
   *
   * @param userDto The user login credentials.
   */
  void verify(UserDtoRegistration userDto);
}
