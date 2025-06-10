package dev.itltcanz.bankapi.controller;

import dev.itltcanz.bankapi.dto.user.UserDtoRegistration;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v1/auth")
@Validated
public interface AuthController {

  @PostMapping("/register")
  @Operation(
      summary = "Register a new user",
      description = "Creates a new user account with the provided credentials")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "User created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "409", description = "User already exists")})
  ResponseEntity<UserDtoResponse> register(
      @Parameter(description = "User registration details (username, password)", required = true)
      @RequestBody @Valid UserDtoRegistration userDto);

  @PostMapping("/login")
  @Operation(
      summary = "Authenticate a user",
      description = "Authenticates a user and returns a JWT token in the response body")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials")})
  ResponseEntity<String> login(
      @Parameter(description = "User login credentials (username, password)", required = true)
      @RequestBody @Valid UserDtoRegistration userDto);
}
