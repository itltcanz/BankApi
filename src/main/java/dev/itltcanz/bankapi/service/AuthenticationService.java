package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.user.UserDtoRegistration;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.entity.enumeration.Role;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.exception.UsernameAlreadyUseException;
import dev.itltcanz.bankapi.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for handling user authentication and registration.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepo userRepo;
  private final PasswordEncoder encoder;
  private final AuthenticationManager authManager;
  private final ModelMapper modelMapper;

  /**
   * Retrieves the currently authenticated user from the security context.
   *
   * @return The authenticated User entity.
   * @throws NotFoundException if the user is not found in the repository.
   */
  public User getCurrentUser() {
    var username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepo.findByUsername(username)
        .orElseThrow(
            () -> new NotFoundException("A user with nickname " + username + " was not found"));
  }

  /**
   * Registers a new user with the provided credentials.
   *
   * @param userDto The user registration details.
   * @return The registered user's details as a DTO.
   * @throws UsernameAlreadyUseException if the username is already taken.
   */
  public UserDtoResponse register(UserDtoRegistration userDto) {
    if (userRepo.existsByUsername(userDto.getUsername())) {
      throw new UsernameAlreadyUseException(
          "A user with nickname " + userDto.getUsername() + " already exists");
    }
    var user = modelMapper.map(userDto, User.class);
    user.setPassword(encoder.encode(user.getPassword()));
    user.setRole(Role.ROLE_USER);
    var savedUser = userRepo.save(user);
    return modelMapper.map(savedUser, UserDtoResponse.class);
  }

  /**
   * Verifies user credentials using the authentication manager.
   *
   * @param userDto The user login credentials.
   */
  public void verify(UserDtoRegistration userDto) {
    authManager.authenticate(
        new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
  }
}
