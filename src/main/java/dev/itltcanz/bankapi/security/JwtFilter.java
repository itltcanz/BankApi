package dev.itltcanz.bankapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter for processing JWT-based authentication in HTTP requests.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtGenerator jwtGenerator;
  private final CustomUserDetailsService customUserDetailsService;
  private final RequestMatcher permitRequests;

  /**
   * Processes the request, authenticating users based on JWT tokens if applicable.
   *
   * @param request     The HTTP servlet request.
   * @param response    The HTTP servlet response.
   * @param filterChain The filter chain to proceed with.
   * @throws ServletException If a servlet error occurs.
   * @throws IOException      If an I/O error occurs.
   */
  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    if (permitRequests.matches(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    var authHeader = request.getHeader("Authorization");
    String token = null;
    String username = null;
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      token = authHeader.substring(7);
      username = jwtGenerator.extractUsername(token);
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      var userDetails = customUserDetailsService.loadUserByUsername(username);
      jwtGenerator.validateToken(token, userDetails);
      var authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
          userDetails.getAuthorities());
      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authToken);
    }
    filterChain.doFilter(request, response);
  }
}