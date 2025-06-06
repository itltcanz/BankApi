package dev.itltcanz.bankapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service for generating, validating, and extracting information from JWT tokens.
 */
@Service
public class JwtGenerator {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-token-validity}")
  private long accessTokenMillis;

  /**
   * Generates a JWT token for the specified username.
   *
   * @param username The username to include in the token.
   * @return The generated JWT token as a string.
   */
  public String generateToken(String username) {
    var claims = new HashMap<String, Object>();
    return Jwts.builder()
        .claims()
        .add(claims)
        .subject(username)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + accessTokenMillis))
        .and()
        .signWith(getKey())
        .compact();
  }

  /**
   * Retrieves the secret key used for signing JWT tokens.
   *
   * @return The secret key.
   */
  private SecretKey getKey() {
    var keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Extracts the username from a JWT token.
   *
   * @param token The JWT token.
   * @return The username embedded in the token.
   * @throws JwtException If the token is invalid or cannot be parsed.
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts a specific claim from a JWT token using a resolver function.
   *
   * @param token          The JWT token.
   * @param claimsResolver The function to extract the desired claim.
   * @param <T>            The type of the claim.
   * @return The extracted claim.
   * @throws JwtException If the token is invalid or cannot be parsed.
   */
  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    var claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extracts all claims from a JWT token.
   *
   * @param token The JWT token.
   * @return The claims embedded in the token.
   * @throws JwtException If the token is invalid or cannot be parsed.
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * Validates a JWT token against user details.
   *
   * @param token       The JWT token to validate.
   * @param userDetails The user details to validate against.
   * @throws IllegalArgumentException If the token's username does not match or the token is
   *                                  expired.
   */
  public void validateToken(String token, UserDetails userDetails) {
    var username = extractUsername(token);
    if (!username.equals(userDetails.getUsername())) {
      throw new IllegalArgumentException("Token username does not match user details");
    }
    if (isTokenExpired(token)) {
      throw new IllegalArgumentException("JWT token has expired");
    }
  }

  /**
   * Checks if a JWT token is expired.
   *
   * @param token The JWT token.
   * @return {@code true} if the token is expired; {@code false} otherwise.
   */
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Extracts the expiration date from a JWT token.
   *
   * @param token The JWT token.
   * @return The expiration date of the token.
   * @throws JwtException If the token is invalid or cannot be parsed.
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
}