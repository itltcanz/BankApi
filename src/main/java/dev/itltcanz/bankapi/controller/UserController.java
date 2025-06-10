package dev.itltcanz.bankapi.controller;

import dev.itltcanz.bankapi.dto.user.UserDtoCreate;
import dev.itltcanz.bankapi.dto.user.UserDtoPatch;
import dev.itltcanz.bankapi.dto.user.UserDtoPut;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@SecurityRequirement(name = "BearerAuth")
public interface UserController {

  @PostMapping
  @Secured("ROLE_ADMIN")
  @Operation(
      summary = "Create a new user",
      description = "Creates a new user. Requires admin privileges")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "User created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid user data"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")
  })
  ResponseEntity<UserDtoResponse> createUser(
      @Parameter(description = "User creation details", required = true)
      @RequestBody @Valid UserDtoCreate userDto
  );

  @GetMapping("/{userId}")
  @Secured("ROLE_ADMIN")
  @Operation(
      summary = "Retrieve a user by ID",
      description = "Fetches details of a specific user by their ID. Requires admin privileges")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User found"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")
  })
  ResponseEntity<UserDtoResponse> getUserById(
      @Parameter(description = "User ID", required = true)
      @PathVariable @NotNull String userId
  );

  @GetMapping
  @Secured("ROLE_ADMIN")
  @Operation(
      summary = "Retrieve users with pagination",
      description = "Fetches a paginated list of all users for admin users")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")
  })
  ResponseEntity<Page<UserDtoResponse>> getUsers(
      @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size
  );

  @DeleteMapping("/{userId}")
  @Secured("ROLE_ADMIN")
  @Operation(
      summary = "Delete a user",
      description = "Deletes a user by their ID. Requires admin privileges")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "User deleted successfully"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")
  })
  ResponseEntity<Void> deleteUser(
      @Parameter(description = "User ID", required = true)
      @PathVariable @NotNull String userId
  );

  @PutMapping("/{userId}")
  @Secured("ROLE_ADMIN")
  @Operation(
      summary = "Fully update a user",
      description = "Updates all details of a user by their ID. Requires admin privileges")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid user data"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")
  })
  ResponseEntity<UserDtoResponse> updateUser(
      @Parameter(description = "User ID", required = true)
      @PathVariable @NotNull String userId,
      @Parameter(description = "Updated user details", required = true)
      @RequestBody @Valid UserDtoPut userDto
  );

  @PatchMapping("/{userId}")
  @Secured("ROLE_ADMIN")
  @Operation(
      summary = "Partially update a user",
      description = "Updates specific details of a user by their ID. Requires admin privileges")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid user data"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")
  })
  ResponseEntity<UserDtoResponse> patchUser(
      @Parameter(description = "User ID", required = true)
      @PathVariable @NotNull String userId,
      @Parameter(description = "Partial user updates", required = true)
      @RequestBody UserDtoPatch userDto
  );
}