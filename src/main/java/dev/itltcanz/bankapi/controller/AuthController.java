package dev.itltcanz.bankapi.controller;

import dev.itltcanz.bankapi.dto.user.UserDtoRegistration;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.security.JwtGenerator;
import dev.itltcanz.bankapi.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

  private final AuthenticationService authService;
  private final JwtGenerator jwtGenerator;

  @PostMapping("/register")
  @Operation(summary = "Register a new user", description = "Creates a new user account with the provided credentials")
  @ApiResponses({@ApiResponse(responseCode = "201", description = "User created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "409", description = "User already exists")})
  public ResponseEntity<UserDtoResponse> register(
      @Parameter(description = "User registration details (username, password)", required = true) @RequestBody @Valid UserDtoRegistration userDto) {
    var userDtoRegistration = authService.register(userDto);
    return new ResponseEntity<>(userDtoRegistration, HttpStatus.CREATED);
  }

  @PostMapping("/login")
  @Operation(summary = "Authenticate a user", description = "Authenticates a user and returns a JWT token in the response body")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials")})
  public ResponseEntity<String> login(
      @Parameter(description = "User login credentials (username, password)", required = true) @RequestBody @Valid UserDtoRegistration userDto) {
    authService.verify(userDto);
    return ResponseEntity.ok(jwtGenerator.generateToken(userDto.getUsername()));
  }

}
