package dev.itltcanz.bankapi.controller;

import dev.itltcanz.bankapi.dto.request.BlockRequestDtoCreate;
import dev.itltcanz.bankapi.dto.request.BlockRequestDtoResponse;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/block-requests")
@Validated
@SecurityRequirement(name = "BearerAuth")
public interface BlockRequestController {

  @PostMapping
  @Operation(summary = "Create a card block request", description = "Submits a request to block a specified card")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Block request created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid request data"),
      @ApiResponse(responseCode = "404", description = "Card or user not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access")})
  ResponseEntity<BlockRequestDtoResponse> createRequest(
      @Parameter(description = "Block request details", required = true) @RequestBody @Valid BlockRequestDtoCreate dto);

  @GetMapping("/all")
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Retrieve all card block requests", description = "Returns a paginated list of all card block requests for admin users")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Block requests retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")})
  ResponseEntity<Page<BlockRequestDtoResponse>> getRequestsAdmin(
      @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Field to sort by", example = "status") @RequestParam(defaultValue = "status") String sortBy,
      @Parameter(description = "Sort direction (asc/desc)", example = "desc") @RequestParam(defaultValue = "desc") String direction);

  @GetMapping("/my")
  @Operation(summary = "Retrieve user's card block requests", description = "Returns a paginated list of the authenticated user's card block requests")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Block requests retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access")})
  ResponseEntity<Page<BlockRequestDtoResponse>> getRequestsUser(
      @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Field to sort by", example = "status") @RequestParam(defaultValue = "status") String sortBy,
      @Parameter(description = "Sort direction (asc/desc)", example = "desc") @RequestParam(defaultValue = "desc") String direction);

  @GetMapping("/{requestId}")
  @Operation(summary = "Retrieve a card block request", description = "Fetches details of a card block request by its ID")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Block request found"),
      @ApiResponse(responseCode = "404", description = "Block request not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access")})
  ResponseEntity<BlockRequestDtoResponse> getRequest(
      @Parameter(description = "Block request ID", required = true) @PathVariable @NotNull String requestId);

  @PatchMapping("/{requestId}/approve")
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Approve a card block request", description = "Allows an administrator to approve a card block request, blocking the specified card")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Block request approved, card blocked"),
      @ApiResponse(responseCode = "400", description = "Invalid request ID"),
      @ApiResponse(responseCode = "404", description = "Block request or card not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")})
  ResponseEntity<BlockRequestDtoResponse> approveRequest(
      @Parameter(description = "Block request ID", required = true) @PathVariable @NotNull String requestId);

  @PatchMapping("/{requestId}/reject")
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Reject a card block request", description = "Allows an administrator to reject a card block request")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Block request rejected"),
      @ApiResponse(responseCode = "400", description = "Invalid request ID"),
      @ApiResponse(responseCode = "404", description = "Block request not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")})
  ResponseEntity<BlockRequestDtoResponse> rejectRequest(
      @Parameter(description = "Block request ID", required = true) @PathVariable @NotNull String requestId);
}