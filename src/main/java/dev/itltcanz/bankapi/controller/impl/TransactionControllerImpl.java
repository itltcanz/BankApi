package dev.itltcanz.bankapi.controller.impl;

import dev.itltcanz.bankapi.controller.TransactionController;
import dev.itltcanz.bankapi.dto.transaction.TransactionDtoCreate;
import dev.itltcanz.bankapi.dto.transaction.TransactionDtoResponse;
import dev.itltcanz.bankapi.service.impl.TransactionServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "BearerAuth")
public class TransactionControllerImpl implements TransactionController {

  private final TransactionServiceImpl transactionService;

  public ResponseEntity<TransactionDtoResponse> createTransaction(
      TransactionDtoCreate transactionDto) {
    return ResponseEntity.ok(transactionService.createTransaction(transactionDto));
  }

  public ResponseEntity<TransactionDtoResponse> getTransactionById(String transactionId) {
    return ResponseEntity.ok(transactionService.getTransactionById(transactionId));
  }

  public ResponseEntity<Page<TransactionDtoResponse>> getAdminTransactions(int page, int size) {
    var pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(transactionService.getAdminTransactions(pageable));
  }

  public ResponseEntity<Page<TransactionDtoResponse>> getMyTransactions(int page, int size) {
    var pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(transactionService.getUserTransactions(pageable));
  }

}