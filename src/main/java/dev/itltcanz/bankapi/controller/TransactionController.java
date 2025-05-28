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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
        description = "Initiates a new transaction with the provided details."
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
}