package dev.itltcanz.bankapi.controller;

import dev.itltcanz.bankapi.dto.card.CardDtoCreate;
import dev.itltcanz.bankapi.dto.card.CardDtoPatch;
import dev.itltcanz.bankapi.dto.card.CardDtoPut;
import dev.itltcanz.bankapi.dto.card.CardDtoResponse;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.filter.CardFilter;
import dev.itltcanz.bankapi.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/v1/cards")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "BearerAuth")
public class CardController {

  private final CardService cardService;

  @PostMapping
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Create a new card", description = "Creates a new card with the provided details. Requires admin privileges")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Card created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid card data"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")})
  public ResponseEntity<CardDtoResponse> createCard(
      @Parameter(description = "Card creation details", required = true) @RequestBody @Valid CardDtoCreate cardDto) {
    var cardResponse = cardService.createCard(cardDto);
    return new ResponseEntity<>(cardResponse, HttpStatus.CREATED);
  }

  @GetMapping("/my")
  @Operation(summary = "Retrieve user's cards", description = "Returns a paginated list of the authenticated user's cards with optional filtering by status and number")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access")})
  public ResponseEntity<Page<CardDtoResponse>> getUserCards(
      @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Field to sort by", example = "balance") @RequestParam(defaultValue = "balance") String sortBy,
      @Parameter(description = "Sort direction (asc/desc)", example = "desc") @RequestParam(defaultValue = "desc") String direction,
      @Parameter(description = "Card status filter") @RequestParam(required = false) CardStatus status,
      @Parameter(description = "Card number filter") @RequestParam(required = false) String number) {
    var sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    var pageable = PageRequest.of(page, size, sort);
    var filter = new CardFilter(status, number, null);
    var cardsResponse = cardService.getUserCards(filter, pageable);
    return ResponseEntity.ok(cardsResponse);
  }

  @GetMapping("/all")
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Retrieve all cards", description = "Returns a paginated list of all cards for admin users with optional filtering by status, number, and owner")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")})
  public ResponseEntity<Page<CardDtoResponse>> getAdminCards(
      @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Field to sort by", example = "balance") @RequestParam(defaultValue = "balance") String sortBy,
      @Parameter(description = "Sort direction (asc/desc)", example = "desc") @RequestParam(defaultValue = "desc") String direction,
      @Parameter(description = "Card status filter") @RequestParam(required = false) CardStatus status,
      @Parameter(description = "Card number filter") @RequestParam(required = false) String number,
      @Parameter(description = "Card owner ID filter") @RequestParam(required = false) UUID ownerId) {
    var sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    var pageable = PageRequest.of(page, size, sort);
    var filter = new CardFilter(status, number, ownerId != null ? ownerId.toString() : null);
    var cardsResponse = cardService.getAdminCards(filter, pageable);
    return ResponseEntity.ok(cardsResponse);
  }

  @GetMapping("/{cardId}")
  @Operation(summary = "Retrieve a card by ID", description = "Fetches details of a specific card by its ID.")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Card found"),
      @ApiResponse(responseCode = "404", description = "Card not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access")})
  public ResponseEntity<CardDtoResponse> getCardById(
      @Parameter(description = "Card ID", required = true) @PathVariable @NotNull String cardId) {
    var cardResponse = cardService.getCardById(cardId);
    return ResponseEntity.ok(cardResponse);
  }

  @PutMapping("/{cardId}")
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Update a card", description = "Fully updates a card's details by its ID. Requires admin privileges")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Card updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid card data"),
      @ApiResponse(responseCode = "404", description = "Card not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")})
  public ResponseEntity<CardDtoResponse> updateCard(
      @Parameter(description = "Card ID", required = true) @PathVariable @NotNull String cardId,
      @Parameter(description = "Updated card details", required = true) @RequestBody @Valid CardDtoPut cardDto) {
    var cardResponse = cardService.updateCard(cardId, cardDto);
    return ResponseEntity.ok(cardResponse);
  }

  @PatchMapping("/{cardId}")
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Partially update a card", description = "Partially updates a card's details by its ID. Requires admin privileges")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Card updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid card data"),
      @ApiResponse(responseCode = "404", description = "Card not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")})
  public ResponseEntity<CardDtoResponse> patchCard(
      @Parameter(description = "Card ID", required = true) @PathVariable @NotNull String cardId,
      @Parameter(description = "Partial card updates", required = true) @RequestBody @Valid CardDtoPatch cardDto) {
    var cardResponse = cardService.updateCard(cardId, cardDto);
    return ResponseEntity.ok(cardResponse);
  }

  @DeleteMapping("/{cardId}")
  @Secured("ROLE_ADMIN")
  @Operation(summary = "Delete a card", description = "Deletes a card by its ID. Requires admin privileges")
  @ApiResponses({@ApiResponse(responseCode = "204", description = "Card deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Card not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized access"),
      @ApiResponse(responseCode = "403", description = "Admin access required")})
  public ResponseEntity<Void> deleteCard(
      @Parameter(description = "Card ID", required = true) @PathVariable @NotNull String cardId) {
    cardService.deleteCard(cardId);
    return ResponseEntity.noContent().build();
  }
}