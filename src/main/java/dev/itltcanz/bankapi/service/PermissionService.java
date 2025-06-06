package dev.itltcanz.bankapi.service;


import dev.itltcanz.bankapi.entity.enumeration.Role;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Service for checking user permissions and access rights.
 */
@Service
@RequiredArgsConstructor
public class PermissionService {

  private final AuthenticationService authService;

  /**
   * Checks if the current user has access to a resource owned by another user.
   *
   * @param compareUserId The ID of the user owning the resource.
   * @throws AccessDeniedException if the current user lacks access.
   */
  public void hasRights(String compareUserId) {
    var currentUser = authService.getCurrentUser();
    if (!currentUser.getRole().equals(Role.ROLE_ADMIN) && !currentUser.getId()
        .equals(UUID.fromString(compareUserId))) {
      throw new AccessDeniedException("Access denied");
    }
  }
}