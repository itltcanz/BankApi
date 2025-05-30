package dev.itltcanz.bankapi.controller;

import dev.itltcanz.bankapi.dto.request.BlockRequestDtoCreate;
import dev.itltcanz.bankapi.dto.request.BlockRequestDtoResponse;
import dev.itltcanz.bankapi.service.BlockRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/block-requests")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "BearerAuth")
public class BlockRequestController {

    private final BlockRequestService blockRequestService;

    @PostMapping
    @Operation(
        summary = "Create a card block request",
        description = "Submits a request to block a specified card"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Block request created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Card or user not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<BlockRequestDtoResponse> createRequest(
        @Parameter(description = "Block request details", required = true)
        @RequestBody @Valid BlockRequestDtoCreate dto) {
        BlockRequestDtoResponse response = blockRequestService.createRequest(dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{requestId}/approve")
    @Secured("ROLE_ADMIN")
    @Operation(
        summary = "Approve a card block request",
        description = "Allows an administrator to approve a card block request, blocking the specified card"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Block request approved, card blocked"),
        @ApiResponse(responseCode = "400", description = "Invalid request ID"),
        @ApiResponse(responseCode = "404", description = "Block request or card not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<BlockRequestDtoResponse> approveRequest(
        @Parameter(description = "Block request ID", required = true)
        @PathVariable @NotNull String requestId) {
        BlockRequestDtoResponse response = blockRequestService.approveRequest(requestId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{requestId}/reject")
    @Secured("ROLE_ADMIN")
    @Operation(
        summary = "Reject a card block request",
        description = "Allows an administrator to reject a card block request"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Block request rejected"),
        @ApiResponse(responseCode = "400", description = "Invalid request ID"),
        @ApiResponse(responseCode = "404", description = "Block request not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<BlockRequestDtoResponse> rejectRequest(
        @Parameter(description = "Block request ID", required = true)
        @PathVariable @NotNull String requestId) {
        BlockRequestDtoResponse response = blockRequestService.rejectRequest(requestId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{requestId}")
    @Operation(
        summary = "Retrieve a card block request",
        description = "Fetches details of a card block request by its ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Block request found"),
        @ApiResponse(responseCode = "404", description = "Block request not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<BlockRequestDtoResponse> getRequest(
        @Parameter(description = "Block request ID", required = true)
        @PathVariable @NotNull String requestId) {
        BlockRequestDtoResponse response = blockRequestService.getRequest(requestId);
        return ResponseEntity.ok(response);
    }
}