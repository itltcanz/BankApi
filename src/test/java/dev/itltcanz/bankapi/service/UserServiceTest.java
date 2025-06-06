package dev.itltcanz.bankapi.service;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import dev.itltcanz.bankapi.dto.user.UserDtoCreate;
import dev.itltcanz.bankapi.dto.user.UserDtoPut;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.entity.enumeration.Role;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.exception.UsernameAlreadyUseException;
import dev.itltcanz.bankapi.repository.UserRepo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepo userRepo;

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private User user;
  private UserDtoCreate userDtoCreate;
  private UserDtoPut userDtoUpdate;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = new User();
    user.setId(userId);
    user.setUsername("testuser");
    user.setPassword("encodedPassword");
    user.setRole(Role.ROLE_USER);

    userDtoCreate = new UserDtoCreate();
    userDtoCreate.setUsername("testuser");
    userDtoCreate.setPassword("password");
    userDtoCreate.setRole(Role.ROLE_USER);

    userDtoUpdate = new UserDtoPut();
    userDtoUpdate.setUsername("updateduser");
    userDtoUpdate.setPassword("newpassword");
    userDtoUpdate.setRole(Role.ROLE_ADMIN);
  }

  @Test
  void createUser_success() {
    when(userRepo.existsByUsername("testuser")).thenReturn(false);
    when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
    when(userRepo.save(any(User.class))).thenReturn(user);
    when(modelMapper.map(user, UserDtoResponse.class)).thenReturn(
        new UserDtoResponse(userId.toString(), "testuser", "ROLE_USER"));

    UserDtoResponse result = userService.createUser(userDtoCreate);

    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
    verify(userRepo).existsByUsername("testuser");
    verify(passwordEncoder).encode("password");
    verify(userRepo).save(any(User.class));
    verify(modelMapper).map(user, UserDtoResponse.class);
  }

  @Test
  void createUser_usernameExists_throwsUsernameAlreadyUseException() {
    when(userRepo.existsByUsername("testuser")).thenReturn(true);

    UsernameAlreadyUseException exception = assertThrows(UsernameAlreadyUseException.class,
        () -> userService.createUser(userDtoCreate));

    assertEquals("Username testuser is already in use", exception.getMessage());
    verify(userRepo).existsByUsername("testuser");
    verifyNoInteractions(passwordEncoder, modelMapper);
  }

  @Test
  void getUsers_success() {
    PageRequest pageable = PageRequest.of(0, 10);
    when(userRepo.findAll(pageable)).thenReturn(new PageImpl<>(List.of(user)));
    when(modelMapper.map(user, UserDtoResponse.class)).thenReturn(new UserDtoResponse());

    var result = userService.getUsers(pageable);

    assertEquals(1, result.getContent().size());
    verify(userRepo).findAll(pageable);
    verify(modelMapper).map(user, UserDtoResponse.class);
  }

  @Test
  void deleteUser_success() {
    when(userRepo.findById(userId)).thenReturn(Optional.of(user));

    userService.deleteUser(userId.toString());

    verify(userRepo).findById(userId);
    verify(userRepo).delete(user);
  }

  @Test
  void deleteUser_notFound_throwsNotFoundException() {
    when(userRepo.findById(userId)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> userService.deleteUser(userId.toString()));

    assertEquals("User with id " + userId + " not found", exception.getMessage());
    verify(userRepo).findById(userId);
    verifyNoMoreInteractions(userRepo);
  }

  @Test
  void updateUser_success() {
    when(userRepo.findById(userId)).thenReturn(Optional.of(user));
    when(userRepo.existsByUsername("updateduser")).thenReturn(false);
    when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
    when(userRepo.save(user)).thenReturn(user);
    when(modelMapper.map(user, UserDtoResponse.class)).thenReturn(new UserDtoResponse());

    UserDtoResponse result = userService.updateUser(userId.toString(), userDtoUpdate);

    assertNotNull(result);
    verify(userRepo).findById(userId);
    verify(userRepo).existsByUsername("updateduser");
    verify(passwordEncoder).encode("newpassword");
    verify(userRepo).save(user);
    verify(modelMapper).map(user, UserDtoResponse.class);
  }

  @Test
  void updateUser_usernameExists_throwsUsernameAlreadyUseException() {
    when(userRepo.findById(userId)).thenReturn(Optional.of(user));
    when(userRepo.existsByUsername("updateduser")).thenReturn(true);

    UsernameAlreadyUseException exception = assertThrows(UsernameAlreadyUseException.class,
        () -> userService.updateUser(userId.toString(), userDtoUpdate));

    assertEquals("Username updateduser is already in use", exception.getMessage());
    verify(userRepo).findById(userId);
    verify(userRepo).existsByUsername("updateduser");
    verifyNoInteractions(passwordEncoder, modelMapper);
  }

  @Test
  void findUserById_success() {
    when(userRepo.findById(userId)).thenReturn(Optional.of(user));

    User result = userService.findUserById(userId.toString());

    assertEquals(user, result);
    verify(userRepo).findById(userId);
  }

  @Test
  void findUserById_notFound_throwsNotFoundException() {
    when(userRepo.findById(userId)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> userService.findUserById(userId.toString()));

    assertEquals("A user with id " + userId + " was not found", exception.getMessage());
    verify(userRepo).findById(userId);
  }

  @Test
  void getUserById_success() {
    when(userRepo.findById(userId)).thenReturn(Optional.of(user));
    when(modelMapper.map(user, UserDtoResponse.class)).thenReturn(new UserDtoResponse());

    UserDtoResponse result = userService.getUserById(userId.toString());

    assertNotNull(result);
    verify(userRepo).findById(userId);
    verify(modelMapper).map(user, UserDtoResponse.class);
  }
}