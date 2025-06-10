package dev.itltcanz.bankapi.service;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import dev.itltcanz.bankapi.dto.user.UserDtoRegistration;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.entity.enumeration.Role;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.exception.UsernameAlreadyUseException;
import dev.itltcanz.bankapi.repository.UserRepo;
import dev.itltcanz.bankapi.service.impl.AuthServiceImpl;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepo userRepo;

  @Mock
  private PasswordEncoder encoder;

  @Mock
  private AuthenticationManager authManager;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private AuthServiceImpl authenticationService;

  private User user;
  private UserDtoRegistration userDto;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("testuser");
    user.setPassword("encodedPassword");
    user.setRole(Role.ROLE_USER);

    userDto = new UserDtoRegistration();
    userDto.setUsername("testuser");
    userDto.setPassword("password");

    // Setup SecurityContextHolder
    Authentication auth = new UsernamePasswordAuthenticationToken("testuser", null);
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @Test
  void getCurrentUser_success() {
    when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

    User result = authenticationService.getCurrentUser();

    assertEquals(user, result);
    verify(userRepo).findByUsername("testuser");
  }

  @Test
  void getCurrentUser_userNotFound_throwsNotFoundException() {
    when(userRepo.findByUsername("testuser")).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> authenticationService.getCurrentUser());

    assertEquals("A user with nickname testuser was not found", exception.getMessage());
    verify(userRepo).findByUsername("testuser");
  }

  @Test
  void register_success() {
    when(userRepo.existsByUsername("testuser")).thenReturn(false);
    when(modelMapper.map(userDto, User.class)).thenReturn(user);
    when(encoder.encode("encodedPassword")).thenReturn("encodedPassword");
    when(userRepo.save(any(User.class))).thenReturn(user);
    when(modelMapper.map(user, UserDtoResponse.class)).thenReturn(
        new UserDtoResponse(user.getId().toString(), "testuser", "ROLE_USER"));

    UserDtoResponse result = authenticationService.register(userDto);

    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
    verify(userRepo).existsByUsername("testuser");
    verify(encoder).encode("encodedPassword");
    verify(userRepo).save(any(User.class));
  }

  @Test
  void register_usernameExists_throwsUsernameAlreadyUseException() {
    when(userRepo.existsByUsername("testuser")).thenReturn(true);

    UsernameAlreadyUseException exception = assertThrows(UsernameAlreadyUseException.class,
        () -> authenticationService.register(userDto));

    assertEquals("A user with nickname testuser already exists", exception.getMessage());
    verify(userRepo).existsByUsername("testuser");
    verifyNoMoreInteractions(modelMapper, encoder, userRepo);
  }

  @Test
  void verify_success() {
    authenticationService.verify(userDto);

    verify(authManager).authenticate(
        new UsernamePasswordAuthenticationToken("testuser", "password"));
  }
}
