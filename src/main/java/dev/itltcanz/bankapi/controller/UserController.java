package dev.itltcanz.bankapi.controller;

import dev.itltcanz.bankapi.dto.user.UserDtoCreate;
import dev.itltcanz.bankapi.dto.user.UserDtoPatch;
import dev.itltcanz.bankapi.dto.user.UserDtoPut;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class UserController {

  private final UserService userService;

  @PostMapping
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Create a new user", description = "Creates a new user. Requires admin privileges")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "User created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid user data"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")
  })
  public ResponseEntity<UserDtoResponse> createUser(
      @Parameter(description = "User creation details", required = true)
      @RequestBody @Valid UserDtoCreate userDto) {
    var createdUser = userService.createUser(userDto);
    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
  }

  @GetMapping("/{userId}")
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Retrieve a user by ID", description = "Fetches details of a specific user by their ID. Requires admin privileges")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User found"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")
  })
  public ResponseEntity<UserDtoResponse> getUserById(
      @Parameter(description = "User ID", required = true)
      @PathVariable @NotNull String userId) {
    var userDto = userService.getUserById(userId);
    return ResponseEntity.ok(userDto);
  }

  @GetMapping
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Retrieve users with pagination", description = "Fetches a paginated list of all users for admin users")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")
  })
  public ResponseEntity<Page<UserDtoResponse>> getUsers(
      @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
    var pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(userService.getUsers(pageable));
  }

  @DeleteMapping("/{userId}")
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Delete a user", description = "Deletes a user by their ID. Requires admin privileges")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "User deleted successfully"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")
  })
  public ResponseEntity<Void> deleteUser(
      @Parameter(description = "User ID", required = true)
      @PathVariable @NotNull String userId) {
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{userId}")
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Fully update a user", description = "Updates all details of a user by their ID. Requires admin privileges")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid user data"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")
  })
  public ResponseEntity<UserDtoResponse> updateUser(
      @Parameter(description = "User ID", required = true)
      @PathVariable @NotNull String userId,
      @Parameter(description = "Updated user details", required = true)
      @RequestBody @Valid UserDtoPut userDto) {
    var updatedUser = userService.updateUser(userId, userDto);
    return ResponseEntity.ok(updatedUser);
  }

  @PatchMapping("/{userId}")
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Partially update a user", description = "Updates specific details of a user by their ID. Requires admin privileges")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid user data"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")
  })
  public ResponseEntity<UserDtoResponse> patchUser(
      @Parameter(description = "User ID", required = true)
      @PathVariable @NotNull String userId,
      @Parameter(description = "Partial user updates", required = true)
      @RequestBody UserDtoPatch userDto) {
    var updatedUser = userService.updateUser(userId, userDto);
    return ResponseEntity.ok(updatedUser);
  }
}