package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.user.UserDto;
import dev.itltcanz.bankapi.dto.user.UserDtoCreate;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.exception.UsernameAlreadyUseException;
import dev.itltcanz.bankapi.repository.UserRepo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for managing user entities.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepo userRepo;
  private final ModelMapper modelMapper;
  private final PasswordEncoder passwordEncoder;

  /**
   * Creates a new user with the provided details.
   *
   * @param userDto The user creation details.
   * @return The created user as a DTO.
   * @throws UsernameAlreadyUseException if the username is already taken.
   */
  public UserDtoResponse createUser(UserDtoCreate userDto) {
    if (userRepo.existsByUsername(userDto.getUsername())) {
      throw new UsernameAlreadyUseException(
          "Username " + userDto.getUsername() + " is already in use");
    }
    var user = new User();
    user.setUsername(userDto.getUsername());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    user.setRole(userDto.getRole());
    var savedUser = userRepo.save(user);
    return modelMapper.map(savedUser, UserDtoResponse.class);
  }

  /**
   * Retrieves a paginated list of all users.
   *
   * @param pageable Pagination parameters.
   * @return A page of user details.
   */
  public Page<UserDtoResponse> getUsers(PageRequest pageable) {
    var userPage = userRepo.findAll(pageable);
    return userPage.map(user -> modelMapper.map(user, UserDtoResponse.class));
  }

  /**
   * Deletes a user by their ID.
   *
   * @param userId The ID of the user.
   * @throws NotFoundException if the user is not found.
   */
  public void deleteUser(String userId) {
    var user = userRepo.findById(UUID.fromString(userId))
        .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    userRepo.delete(user);
  }

  /**
   * Updates a user's details.
   *
   * @param userId  The ID of the user.
   * @param userDto The updated user details.
   * @return The updated user as a DTO.
   * @throws NotFoundException           if the user is not found.
   * @throws UsernameAlreadyUseException if the new username is already taken.
   */
  public UserDtoResponse updateUser(String userId, UserDto userDto) {
    var user = findUserById(userId);
    if (userRepo.existsByUsername(userDto.getUsername())) {
      throw new UsernameAlreadyUseException(
          "Username " + userDto.getUsername() + " is already in use");
    }
    user.setUsername(userDto.getUsername());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    user.setRole(userDto.getRole());
    var savedUser = userRepo.save(user);
    return modelMapper.map(savedUser, UserDtoResponse.class);
  }

  /**
   * Retrieves a user by their ID.
   *
   * @param userId The ID of the user.
   * @return The user entity.
   * @throws NotFoundException if the user is not found.
   */
  public User findUserById(String userId) {
    return userRepo.findById(UUID.fromString(userId))
        .orElseThrow(() -> new NotFoundException("A user with id " + userId + " was not found"));
  }

  /**
   * Retrieves a user by their ID as a DTO.
   *
   * @param userId The ID of the user.
   * @return The user details as a DTO.
   * @throws NotFoundException if the user is not found.
   */
  public UserDtoResponse getUserById(String userId) {
    return modelMapper.map(findUserById(userId), UserDtoResponse.class);
  }
}