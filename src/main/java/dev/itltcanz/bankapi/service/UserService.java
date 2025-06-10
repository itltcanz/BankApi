package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.user.UserDto;
import dev.itltcanz.bankapi.dto.user.UserDtoCreate;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.exception.UsernameAlreadyUseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Service for managing user entities.
 */
public interface UserService {

  /**
   * Creates a new user with the provided details.
   *
   * @param userDto The user creation details.
   * @return The created user as a DTO.
   * @throws UsernameAlreadyUseException if the username is already taken.
   */
  UserDtoResponse createUser(UserDtoCreate userDto);

  /**
   * Retrieves a paginated list of all users.
   *
   * @param pageable Pagination parameters.
   * @return A page of user details.
   */
  Page<UserDtoResponse> getUsers(PageRequest pageable);

  /**
   * Deletes a user by their ID.
   *
   * @param userId The ID of the user.
   * @throws NotFoundException if the user is not found.
   */
  void deleteUser(String userId);

  /**
   * Updates a user's details.
   *
   * @param userId  The ID of the user.
   * @param userDto The updated user details.
   * @return The updated user as a DTO.
   * @throws NotFoundException           if the user is not found.
   * @throws UsernameAlreadyUseException if the new username is already taken.
   */
  UserDtoResponse updateUser(String userId, UserDto userDto);

  /**
   * Retrieves a user by their ID.
   *
   * @param userId The ID of the user.
   * @return The user entity.
   * @throws NotFoundException if the user is not found.
   */
  User findUserById(String userId);

  /**
   * Retrieves a user by their ID as a DTO.
   *
   * @param userId The ID of the user.
   * @return The user details as a DTO.
   * @throws NotFoundException if the user is not found.
   */
  UserDtoResponse getUserById(String userId);
}