package dev.itltcanz.bankapi.service;


import org.springframework.security.access.AccessDeniedException;

/**
 * Service for checking user permissions and access rights.
 */
public interface PermissionService {

  /**
   * Checks if the current user has access to a resource owned by another user.
   *
   * @param compareUserId The ID of the user owning the resource.
   * @throws AccessDeniedException if the current user lacks access.
   */
  void hasRights(String compareUserId);
}