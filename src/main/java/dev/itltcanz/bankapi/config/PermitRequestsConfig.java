package dev.itltcanz.bankapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Configuration for defining permitted HTTP requests that bypass authentication.
 */
@Configuration
public class PermitRequestsConfig {

  /**
   * Defines a RequestMatcher for endpoints that do not require authentication.
   *
   * @return RequestMatcher for permitted endpoints
   */
  @Bean
  public RequestMatcher permitRequests() {
    return new OrRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/v1/auth/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/swagger-ui/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/v3/api-docs/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/api-docs/**"));
  }
}
