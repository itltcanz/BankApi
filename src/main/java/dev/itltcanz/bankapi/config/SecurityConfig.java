package dev.itltcanz.bankapi.config;

import dev.itltcanz.bankapi.security.CustomUserDetailsService;
import dev.itltcanz.bankapi.security.JwtExceptionFilter;
import dev.itltcanz.bankapi.security.JwtFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Security configuration for the application, enabling JWT-based authentication and CORS.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;
  private final JwtFilter jwtFilter;
  private final JwtExceptionFilter jwtExceptionFilter;
  private final RequestMatcher permitRequests;

  /**
   * Configures the security filter chain with JWT authentication and CORS settings.
   *
   * @param httpSecurity the HttpSecurity configuration
   * @return the configured SecurityFilterChain
   * @throws Exception if configuration fails
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(
            request -> request.requestMatchers(permitRequests).permitAll().anyRequest().authenticated())
        .cors(cors -> cors.configurationSource(request -> {
          var corsConfiguration = new CorsConfiguration();
          corsConfiguration.setAllowedOrigins(
              List.of("http://localhost:8080", "http://localhost:3000"));
          corsConfiguration.setAllowedMethods(
              List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
          corsConfiguration.setAllowedHeaders(List.of("*"));
          corsConfiguration.setAllowCredentials(true);
          return corsConfiguration;
        })).sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter, JwtFilter.class);
    return httpSecurity.build();
  }

  /**
   * Configures the authentication manager.
   *
   * @param config the AuthenticationConfiguration
   * @return the configured AuthenticationManager
   * @throws Exception if configuration fails
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * Configures the authentication provider with a custom user details service.
   *
   * @return the configured AuthenticationProvider
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    var provider = new DaoAuthenticationProvider(customUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  /**
   * Configures the password encoder using BCrypt.
   *
   * @return the BCryptPasswordEncoder instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }
}