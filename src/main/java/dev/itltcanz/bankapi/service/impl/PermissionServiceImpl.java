package dev.itltcanz.bankapi.service.impl;


import dev.itltcanz.bankapi.entity.enumeration.Role;
import dev.itltcanz.bankapi.service.PermissionService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service("permissionService")
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

  private final AuthServiceImpl authService;

  @Override
  public void hasRights(String compareUserId) {
    var currentUser = authService.getCurrentUser();
    if (!currentUser.getRole().equals(Role.ROLE_ADMIN) && !currentUser.getId()
        .equals(UUID.fromString(compareUserId))) {
      throw new AccessDeniedException("Access denied");
    }
  }
}