package dev.itltcanz.bankapi.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter for handling JWT-related exceptions during request processing.
 */
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

  /**
   * Processes the request, catching and handling JWT-related exceptions.
   *
   * @param request     The HTTP servlet request.
   * @param response    The HTTP servlet response.
   * @param filterChain The filter chain to proceed with.
   * @throws ServletException If a servlet error occurs.
   * @throws IOException      If an I/O error occurs.
   */
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (SignatureException ex) {
      handleException(response, "Wrong JWT token");
    } catch (ExpiredJwtException ex) {
      handleException(response, "JWT token has expired");
    }
  }

  /**
   * Handles JWT exceptions by setting an unauthorized response with an error message.
   *
   * @param response  The HTTP servlet response.
   * @param errorText The error message to include in the response.
   * @throws IOException If an I/O error occurs while writing the response.
   */
  private void handleException(HttpServletResponse response, String errorText) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    response.getWriter().write("{\"error\": \"" + errorText + "\"}");
  }
}