package dev.itltcanz.bankapi.security;

import dev.itltcanz.bankapi.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for loading user details for authentication purposes.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepo userRepo;

  /**
   * Loads user details by username for authentication.
   *
   * @param username The username to load details for.
   * @return The UserDetails object representing the user.
   * @throws UsernameNotFoundException if the user is not found.
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepo.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
  }
}
