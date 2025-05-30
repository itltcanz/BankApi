package dev.itltcanz.bankapi.controller;

import dev.itltcanz.bankapi.dto.user.UserDtoRegistration;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.security.JwtGenerator;
import dev.itltcanz.bankapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final UserService userService;
    private final JwtGenerator jwtGenerator;

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided credentials"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<UserDtoResponse> register(
        @Parameter(description = "User registration details", required = true)
        @RequestBody @Valid UserDtoRegistration userDto
    ) {
        var userDtoRegistration = userService.register(userDto);
        return ResponseEntity.ok(userDtoRegistration);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate a user",
        description = "Authenticates a user and returns a JWT token"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<String> login(
        @Parameter(description = "User login credentials", required = true)
        @RequestBody @Valid UserDtoRegistration userDto) {
        userService.verify(userDto);
        return ResponseEntity.ok(jwtGenerator.generateToken(userDto.getUsername()));
    }
}
