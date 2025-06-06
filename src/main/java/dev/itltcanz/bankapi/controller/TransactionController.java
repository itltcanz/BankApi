package dev.itltcanz.bankapi.controller;

import dev.itltcanz.bankapi.dto.transaction.TransactionDtoCreate;
import dev.itltcanz.bankapi.dto.transaction.TransactionDtoResponse;
import dev.itltcanz.bankapi.service.TransactionService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "BearerAuth")
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping
  @Operation(
      summary = "Create a new transaction",
      description = "Initiates a new transaction with the provided details"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transaction created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid transaction data"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access")
  })
  public ResponseEntity<TransactionDtoResponse> createTransaction(
      @Parameter(description = "Transaction details", required = true)
      @RequestBody @Valid TransactionDtoCreate transactionDto) {
    return ResponseEntity.ok(transactionService.createTransaction(transactionDto));
  }

  @GetMapping("/{transactionId}")
  @Operation(
      summary = "Retrieve a transaction by ID",
      description = "Fetches details of a specific transaction by its ID with permission checks"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transaction found"),
      @ApiResponse(responseCode = "404", description = "Transaction not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access")
  })
  public ResponseEntity<TransactionDtoResponse> getTransactionById(
      @Parameter(description = "Transaction ID", required = true)
      @PathVariable @NotNull String transactionId
  ) {
    return ResponseEntity.ok(transactionService.findByIdWithPermissionCheck(transactionId));
  }

  @GetMapping("/all")
  @Secured("ROLE_ADMIN")
  @Operation(
      summary = "Retrieve all transactions",
      description = "Returns a paginated list of all transactions for admin users"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")
  })
  public ResponseEntity<Page<TransactionDtoResponse>> getAllTransactions(
      @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size
  ) {
    var pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(transactionService.getTransactionsAdmin(pageable));
  }

  @GetMapping("/my")
  @Operation(
      summary = "Retrieve user's transactions",
      description = "Returns a paginated list of the authenticated user's transactions"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access")
  })
  public ResponseEntity<Page<TransactionDtoResponse>> getMyTransactions(
      @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size
  ) {
    var pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(transactionService.getTransactionsUser(pageable));
  }

}