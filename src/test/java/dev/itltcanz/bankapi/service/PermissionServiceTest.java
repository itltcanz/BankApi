package dev.itltcanz.bankapi.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.entity.enumeration.Role;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

  @Mock
  private AuthenticationService authService;

  @InjectMocks
  private PermissionService permissionService;

  private User user;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = new User();
    user.setId(userId);
    user.setRole(Role.ROLE_USER);
  }

  @Test
  void hasRights_adminUser_success() {
    user.setRole(Role.ROLE_ADMIN);
    when(authService.getCurrentUser()).thenReturn(user);

    assertDoesNotThrow(() -> permissionService.hasRights(userId.toString()));
    verify(authService).getCurrentUser();
  }

  @Test
  void hasRights_sameUserId_success() {
    when(authService.getCurrentUser()).thenReturn(user);

    assertDoesNotThrow(() -> permissionService.hasRights(userId.toString()));
    verify(authService).getCurrentUser();
  }

  @Test
  void hasRights_differentUserId_throwsAccessDeniedException() {
    UUID differentUserId = UUID.randomUUID();
    when(authService.getCurrentUser()).thenReturn(user);

    AccessDeniedException exception = assertThrows(AccessDeniedException.class,
        () -> permissionService.hasRights(differentUserId.toString()));

    assertEquals("Access denied", exception.getMessage());
    verify(authService).getCurrentUser();
  }
}